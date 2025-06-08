package bind.auth.service;

import bind.auth.entity.User;


import event.constant.EventType;
import event.domain.Event;
import event.dto.EmailVerificationEventPayload;
import event.dto.UserRegisteredEventPayload;
import event.dto.UserWithdrawEventPayload;
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
    private final AuthService authService;
    private final OutboxPublisher outboxService;

    /**
     * 이메일 인증 이벤트 발행
     */
    public void emailVerification(User user) {
        log.info("called emailVerification");
        String token = tokenProvider.createAccessToken(authService.tokenParams(user.getId()));

        EmailVerificationEventPayload payload = new EmailVerificationEventPayload(
                user.getId(), token ,user.getEmail()
        );

        Event<EmailVerificationEventPayload> event = new Event<>(
                EventType.EMAIL_VERIFICATION,
                System.currentTimeMillis(),
                payload
        );

        outboxService.saveEvent("user-email-verification-topic", event);
    }

    /**
     * 회원가입 이벤트 발행
     */
    public void userRegistered(User user) {
        log.info("called userRegistered");
        String token = tokenProvider.createAccessToken(authService.tokenParams(user.getId()));

        UserRegisteredEventPayload payload = new UserRegisteredEventPayload(user.getId(), token);

        Event<UserRegisteredEventPayload> event = new Event<>(
                EventType.USER_REGISTERED,
                System.currentTimeMillis(),
                payload
        );

        outboxService.saveEvent("user-registered-topic", event);
    }

    /**
     * 회원탈퇴 이벤트 발행
     */
    public void userWithdrawal(User user) {
        log.info("called userWithdrawal");

        UserWithdrawEventPayload payload = new UserWithdrawEventPayload(user.getId(), user.getEmail());

        Event<UserWithdrawEventPayload> event = new Event<>(
                EventType.USER_WITHDRAWN,
                System.currentTimeMillis(),
                payload
        );

        outboxService.saveEvent("user-withdrawal-topic",  event);
    }
}
