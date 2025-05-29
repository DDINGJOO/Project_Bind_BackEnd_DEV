package event.dto;

import event.constant.EventType;
import event.domain.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmailVerificationEvent extends BaseEvent {
    private String referenceId;
    private String token;
    private String email;

    public EmailVerificationEvent(String referenceId, String email, String token) {
        super(EventType.EMAIL_VERIFICATION);
        this.referenceId = referenceId;
        this.token = token;
        this.email = email;
    }

}


