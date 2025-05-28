package event.dto;

import event.constant.EventType;
import event.domain.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProfileUpdatedEvent extends BaseEvent {
    private String userId;
    private Long imageId;


    public ProfileUpdatedEvent(String userId,Long imageId) {
        super(EventType.PROFILE_UPDATED);
        this.userId = userId;
        this.imageId = imageId;

    }
}
