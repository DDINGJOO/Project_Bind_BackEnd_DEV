package bind.auth.controller;


import bind.auth.dto.request.WithdrawRequest;
import bind.auth.exception.AuthException;
import bind.auth.service.AuthService;
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

@Tag(name = "Auth - Withdraw", description = "회원 탈퇴 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthWithdrawController {

    private final AuthService authService;

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
            @Parameter(description = "탈퇴할 사용자 ID", required = true) @RequestParam String userId,
            @Valid @RequestBody WithdrawRequest request
    ) {
        try {
            authService.withdraw(userId, request);
            return ResponseEntity.ok(BaseResponse.success());
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("알 수 없는 오류가 발생했습니다."));
        }
    }
}
