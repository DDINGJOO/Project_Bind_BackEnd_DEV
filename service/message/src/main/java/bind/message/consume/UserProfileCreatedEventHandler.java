package bind.message.consume;


import bind.message.service.UserProfileSnapshotService;
import event.constant.EventType;
import event.domain.Event;
import event.dto.UserProfileCreatedEventPayload;
import event.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileCreatedEventHandler  implements EventHandler<UserProfileCreatedEventPayload> {
    private final UserProfileSnapshotService userProfileSnapshotService;

    @Override
    public EventType supportedType() {
        return EventType.USER_PROFILE_CREATED;
    }

    @Override
    public void handle(Event<UserProfileCreatedEventPayload> event) {
        // 실제 사용자 프로필 업데이트 후 처리할 비즈니스 로직 수행
        UserProfileCreatedEventPayload payload = event.getPayload();

        userProfileSnapshotService.saveUser(payload);

    }



}
