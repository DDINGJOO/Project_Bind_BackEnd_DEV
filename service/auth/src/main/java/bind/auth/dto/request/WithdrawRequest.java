package bind.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원 탈퇴 요청 DTO")
public record WithdrawRequest(
        @Schema(description = "탈퇴 사유", example = "더 이상 서비스를 사용하지 않음")
        @NotBlank(message = "탈퇴 사유는 필수입니다.")
        String reason
) {}
