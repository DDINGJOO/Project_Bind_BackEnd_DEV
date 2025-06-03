package bind.message.consume;


import bind.message.service.UserProfileSnapshotService;
import event.constant.EventType;
import event.domain.Event;
import event.dto.ProfileUpdatedEventPayload;
import event.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileUpdateEventHandler  implements EventHandler<ProfileUpdatedEventPayload> {
    private final UserProfileSnapshotService userProfileSnapshotService;

    @Override
    public EventType supportedType() {
        return EventType.USER_PROFILE_UPDATED;
    }

    @Override
    public void handle(Event<ProfileUpdatedEventPayload> event) {
        // 실제 사용자 프로필 업데이트 후 처리할 비즈니스 로직 수행
        ProfileUpdatedEventPayload payload = event.getPayload();

        userProfileSnapshotService.upDateUser(payload);

    }



}

/*

@Component
@RequiredArgsConstructor
public class UserWithdrawEventHandler  implements EventHandler<UserWithdrawEventPayload> {

    private final MailService mailService;

    @Override
    public EventType supportedType() {
        return EventType.USER_WITHDRAWN;
    }

    @Override
    public void handle(Event<UserWithdrawEventPayload> event) {
        // 실제 사용자 등록 후 처리할 비즈니스 로직 수행
        UserWithdrawEventPayload payload = event.getPayload();
        System.out.println("회원 탈퇴 이벤트 수신: " + payload.getEmail());

        mailService.sendGoodByeMail(payload);
    }


    }

 */
