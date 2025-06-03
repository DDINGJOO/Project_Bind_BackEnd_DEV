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
    private String profileImageUrl;
    private String nickname;


    public ProfileCreatedEvent(String userId,String profileImageUrl, String nickname) {
        super(EventType.USER_PROFILE_CREATED);
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
        this.nickname = nickname;

    }
}
