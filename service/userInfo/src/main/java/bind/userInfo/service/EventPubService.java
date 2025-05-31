package bind.userInfo.service;


import event.constant.EventType;
import event.domain.Event;
import event.dto.ProfileCreatedEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import outbox.OutboxPublisher;
import security.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
@Slf4j

public class EventPubService {
    private final JwtProvider tokenProvider;
    private final UserProfileService userProfileService;
    private final OutboxPublisher outboxService;


    public void userProfileCreatedEvent(String userId) {
        log.info("called userProfileCreatedEvent");

        ProfileCreatedEventPayload event = new ProfileCreatedEventPayload(userId);

        // outboxService.saveMessage(토픽명, 키, 이벤트객체);
        Event<ProfileCreatedEventPayload>
                profileCreatedEvent = new Event<>(
                EventType.USER_PROFILE_CREATED,
                System.currentTimeMillis(),
                event
        );
        outboxService.saveEvent("user-profile-created-topic", profileCreatedEvent);

    }
/*
    public void emailVerification(User user) {
        log.info("called emailVerification");
        String token = tokenProvider.createAccessToken(authService.tokenParams(user.getId()));

        EmailVerificationEventPayload payload = new EmailVerificationEventPayload(
                user.getId(), user.getEmail(), token
        );

        Event<EmailVerificationEventPayload> event = new Event<>(
                EventType.EMAIL_VERIFICATION,
                System.currentTimeMillis(),
                payload
        );

        outboxService.saveEvent("user-email-verification-topic", event);
    }
 */
}
