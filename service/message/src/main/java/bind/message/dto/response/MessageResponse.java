package bind.message.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record MessageResponse(
        @NotBlank
        String SenderId,

        String senderNickName,
        String senderProfileImageUrl,

        @NotBlank
        String receiverId,
        String receiverNickName,
        String receiverProfileImageUrl,

        @NotBlank
        String subject,

        @NotBlank
        String contents,

        @NotBlank
        LocalDateTime createdAt
) {
}
