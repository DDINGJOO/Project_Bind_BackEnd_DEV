package bind.auth.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

public record WithdrawRequest(
        UUID userId,

        @NotBlank(message = "탈퇴 사유는 필수입니다")
        String reason
) {}
