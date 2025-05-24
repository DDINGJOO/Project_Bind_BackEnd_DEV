package bind.auth.dto.response;

import java.time.LocalDateTime;

public record UserSuspensionStatusResponse(
        String userId,
        boolean isSuspended,
        String reason,
        LocalDateTime suspendedAt,
        LocalDateTime releaseAt
) {}
