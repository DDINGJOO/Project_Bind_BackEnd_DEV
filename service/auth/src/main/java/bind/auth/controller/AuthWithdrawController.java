package bind.auth.controller;


import bind.auth.dto.request.WithdrawRequest;
import bind.auth.entity.User;
import bind.auth.exception.AuthErrorCode;
import bind.auth.exception.AuthException;
import bind.auth.service.AuthService;
import bind.auth.service.EventPubService;
import data.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import security.jwt.JwtProvider;

@Tag(name = "Auth - Withdraw", description = "회원 탈퇴 관련 API")
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AuthWithdrawController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final EventPubService eventPubService;

    /**
     * 회원 탈퇴 API
     * @param userId 사용자 ID
     * @param request 탈퇴 요청 정보
     * @return 탈퇴 성공 여부
     */
    @Operation(
            summary = "회원 탈퇴",
            description = "해당 사용자의 계정을 비활성화하고 탈퇴 이력을 기록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                    @ApiResponse(responseCode = "400", description = "탈퇴 처리 실패", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
            }
    )
    @PostMapping("/withdraw")
    public ResponseEntity<BaseResponse<Void>> withdraw(
            @RequestHeader("Authorization") String bearerToken,
            @Parameter(description = "탈퇴할 사용자 ID", required = true) @RequestParam String userId,
            @Valid @RequestBody WithdrawRequest request
    ) {

        // 토큰 검증 및 사용자 ID 추출
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(AuthErrorCode.TOKEN_INVALID));
        }
        String token = bearerToken.substring(7);
        String tokenUserId = jwtProvider.getUserIdFromToken(token);
        if (!tokenUserId.equals(userId)) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(AuthErrorCode.TOKEN_INVALID));
        }


        try {
            User user =authService.withdraw(userId, request);
            eventPubService.userWithdrawal(user);
            return ResponseEntity.ok(BaseResponse.success());
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("알 수 없는 오류가 발생했습니다."));
        }


    }
}
