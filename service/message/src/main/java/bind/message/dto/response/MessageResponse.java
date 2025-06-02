package bind.message.dto.response;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record MessageResponse(
        @NotBlank
        String SenderId,

        @NotBlank
        String receiverId,

        @NotBlank
        String subject,

        @NotBlank
        String contents,

        @NotBlank
        LocalDateTime createdAt
) {
}
