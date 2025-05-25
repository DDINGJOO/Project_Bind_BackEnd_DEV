package bind.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Builder
public record UserSuspensionStatusResponse(
        String userId,
        boolean isSuspended,
        String reason,
        LocalDateTime suspendedAt,
        LocalDateTime releaseAt
) {

}
