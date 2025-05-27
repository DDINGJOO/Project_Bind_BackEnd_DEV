package bind.auth.controller;

import bind.auth.dto.request.LoginRequest;
import bind.auth.dto.request.PasswordChangeRequest;
import bind.auth.dto.request.RegisterRequest;
import bind.auth.dto.response.LoginResponse;
import bind.auth.entity.User;
import bind.auth.exception.AuthException;
import bind.auth.service.AuthService;
import bind.auth.service.EventPubService;
import data.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import security.jwt.JwtProvider;

/**
 * 인증 관련 기능을 제공하는 컨트롤러입니다.
 */
@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final EventPubService eventPubService;
    private final JwtProvider jwtProvider;

    /**
     * 회원가입 API
     * @param request 회원가입 요청 (로그인 ID, 비밀번호 등)
     */
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Call Resister");
        User user;
        try {
            user = authService.register(request);
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("AuthService를 수행하는중 발생했습니다."));
        }
        try{
            eventPubService.kafkaEmailVerification(user);
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("카프카 메세지 발행 중 오류가 발생했습니다."));
        }
        return ResponseEntity.ok(BaseResponse.success());
    }

    /**
     * 로그인 API
     * @param request 로그인 요청 (로그인 ID, 비밀번호, 디바이스 ID)
     * @param userAgent 사용자 브라우저 정보
     * @param clientIp 클라이언트 IP (프록시 환경 고려)
     */
    @Operation(summary = "로그인", description = "사용자 로그인 및 토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest request,
            @RequestHeader("User-Agent") String userAgent,
            @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp) {
        try {
            String ip = (clientIp != null) ? clientIp : "127.0.0.1";
            LoginResponse response = authService.login(request, ip, userAgent);
            return ResponseEntity.ok(BaseResponse.success(response));
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }

    /**
     * 토큰 재발급 API
     * @param userId 사용자 ID
     *               @param deviceId 디바이스 ID
     *                               @param refreshToken 리프레시 토큰
     *
     */
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 통한 새로운 엑세스/리프레시 토큰 발급")
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<LoginResponse>> refresh(
            @RequestParam String userId,
            @RequestParam String deviceId,
            @RequestParam String refreshToken) {
        try {
            LoginResponse tokens = authService.refresh(userId, deviceId, refreshToken);
            return ResponseEntity.ok(BaseResponse.success(tokens));
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }



    /**
     * 비밀번호 변경 API
     * @param logindId 사용자 로그인ID
     * @param request 비밀번호 변경 요청 (현재 비밀번호, 새 비밀번호)
     */
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 새 비밀번호로 변경합니다.")
    @PutMapping("/change-password")
    public ResponseEntity<BaseResponse<Void>> changePassword(
            @RequestParam String logindId,
            @RequestBody @Valid PasswordChangeRequest request
    ) {
        try {
            authService.changePassword(logindId, request);
            return ResponseEntity.ok(BaseResponse.success());
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }


    /**
     * 로그아웃 처리 API
     * @param userId 사용자 ID
     * @param deviceId 디바이스 ID
     */
    @Operation(summary = "로그아웃", description = "해당 디바이스에서 로그아웃 처리")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(
            @RequestParam String userId,
            @RequestParam String deviceId) {
        try {
            authService.logout(userId, deviceId);
            return ResponseEntity.ok(BaseResponse.success());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("로그아웃 처리 중 오류 발생"));
        }
    }


    @GetMapping("/health")
    public ResponseEntity<BaseResponse<String>> healthCheck() {
        return ResponseEntity.ok(BaseResponse.success("Auth service is running"));
    }

    /**
     * 마이 페이지 API
     * @return 마이 페이지 정보
     */
    @GetMapping("/my-id")
    public ResponseEntity<BaseResponse<String>> getMyId(
            @RequestHeader("Authorization") String bearerToken
    ) {
        try {
            String userId = jwtProvider.getUserIdFromToken(bearerToken);
            return ResponseEntity.ok(BaseResponse.success(userId));
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("알 수 없는 오류가 발생했습니다."));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<BaseResponse<Void>> confirmEmail(
            @RequestParam String token
    ) {
        try {
            authService.confirmEmail(token);
            return ResponseEntity.ok(BaseResponse.success());
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("알 수 없는 오류가 발생했습니다."));
        }
    }

}
