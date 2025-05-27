package event.dto;

import event.constant.EventType;
import event.domain.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRegisteredEvent extends BaseEvent {
    private String userId;
    private String email;
    private String token;

    public UserRegisteredEvent(String userId, String email,String token) {
        super(EventType.USER_REGISTERED);
        this.userId = userId;
        this.email = email;
        this.token = token; // Token can be set later if needed
    }
}
