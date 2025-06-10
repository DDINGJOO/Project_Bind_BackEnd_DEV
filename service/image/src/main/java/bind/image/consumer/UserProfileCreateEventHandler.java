package bind.image.consumer;

import bind.image.service.ImageFileService;
import bind.image.service.eventPub.ImageFileEventPub;
import data.enums.ResourceCategory;
import event.constant.EventType;
import event.domain.Event;
import event.dto.UserProfileCreatedEventPayload;
import event.handler.EventHandler;

public class UserProfileCreateEventHandler implements EventHandler<UserProfileCreatedEventPayload> {
    private ImageFileService imageService;
    private ImageFileEventPub imageFileEventPub;

    @Override
    public EventType supportedType() {
        return EventType.USER_PROFILE_CREATED;
    }

    @Override
    public void handle(Event<UserProfileCreatedEventPayload> event) {
        UserProfileCreatedEventPayload payload = event.getPayload();
        System.out.println("유저 프로필 생성 이벤트 수신" + payload.getUserId());

        if (payload.getProfileImageUrl() != null) {
            imageService.markAsPendingDeleteExceptTemp(ResourceCategory.PROFILE, payload.getUserId());
        }


    }
}

/*
@Component
@RequiredArgsConstructor
public class UserEmailVerificationEventHandler implements EventHandler<EmailVerificationEventPayload> {

    private final MailService mailService;

    @Override
    public EventType supportedType() {
        return EventType.EMAIL_VERIFICATION;
    }

    @Override
    public void handle(Event<EmailVerificationEventPayload> event) {
        // 실제 사용자 등록 후 처리할 비즈니스 로직 수행
        EmailVerificationEventPayload payload = event.getPayload();
        System.out.println("이메일 인증 이벤트 수신: " + payload.getEmail());

        mailService.sendVerificationEmail(payload);
    }
}

 */
