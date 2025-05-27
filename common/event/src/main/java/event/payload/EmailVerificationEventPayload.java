package event.payload;


import event.events.EventPayload;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationEventPayload implements EventPayload {
    private String userId;
    private String email;
    private String verificationToken;
}
