package bind.auth.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record UserSuspensionRequest(
        @NotNull(message = "정지 대상 유저 ID는 필수입니다.")
        String userId,

        @NotBlank(message = "정지 사유는 필수입니다.")
        String reason,

        // null이면 영구정지
        LocalDateTime releaseAt
) {

}
