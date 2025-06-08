package event.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import event.constant.EventType;
import event.domain.EventPayload;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeName("EmailVerificationEventPayload")
public class EmailVerificationEventPayload implements EventPayload {
    private String referenceId;
    private String token;
    private String email;
}
