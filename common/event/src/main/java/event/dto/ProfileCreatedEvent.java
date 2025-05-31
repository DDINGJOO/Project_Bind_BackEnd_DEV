package event.dto;

import event.constant.EventType;
import event.domain.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProfileCreatedEvent extends BaseEvent {
    private String userId;



    public ProfileCreatedEvent(String userId) {
        super(EventType.USER_PROFILE_CREATED);
        this.userId = userId;

    }
}
