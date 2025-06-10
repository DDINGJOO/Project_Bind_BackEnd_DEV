package bind.message.consume;


import bind.message.service.UserProfileSnapshotService;
import event.constant.EventType;
import event.domain.Event;
import event.dto.UserProfileUpdatedEventPayload;
import event.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileUpdateEventHandler  implements EventHandler<UserProfileUpdatedEventPayload> {
    private final UserProfileSnapshotService userProfileSnapshotService;

    @Override
    public EventType supportedType() {
        return EventType.USER_PROFILE_NICKNAME_UPDATED;
    }

    @Override
    public void handle(Event<UserProfileUpdatedEventPayload> event) {
        // 실제 사용자 프로필 업데이트 후 처리할 비즈니스 로직 수행
        UserProfileUpdatedEventPayload payload = event.getPayload();

        userProfileSnapshotService.upDateUser(payload);

    }



}

