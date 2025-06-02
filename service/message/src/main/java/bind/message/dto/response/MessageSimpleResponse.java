package bind.message.dto.response;


import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record MessageSimpleResponse(

        @NotBlank
        String SenderId,

        @NotBlank
        String receiverId,

        @NotBlank
        String subject,

        @NotBlank
        LocalDateTime createdAt
) {
}
