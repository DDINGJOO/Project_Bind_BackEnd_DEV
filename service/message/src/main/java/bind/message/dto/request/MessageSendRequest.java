package bind.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



public record MessageSendRequest(
        @NotBlank
        String receiverId,
        @NotBlank
        String subject,
        @NotBlank
        String content
) {
}
