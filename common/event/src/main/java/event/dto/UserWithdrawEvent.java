package event.dto;

import event.constant.EventType;
import event.domain.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserWithdrawEvent extends BaseEvent {

    private String email;


    public UserWithdrawEvent(String email) {
        super(EventType.USER_WITHDRAWN);

        this.email = email;

    }
}
