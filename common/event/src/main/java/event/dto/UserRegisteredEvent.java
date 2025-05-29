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
    private String token;

    public UserRegisteredEvent(String userId,String token) {
        super(EventType.USER_REGISTERED);
        this.userId = userId;
        this.token = token;
    }
}
