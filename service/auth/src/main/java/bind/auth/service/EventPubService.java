package bind.auth.service;

import bind.auth.entity.User;


import event.dto.EmailVerificationEvent;
import event.dto.UserRegisteredEvent;
import event.dto.UserWithdrawEvent;
import event.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import outbox.OutboxService;
import security.jwt.JwtProvider;



@Service
@RequiredArgsConstructor
@Slf4j
public class EventPubService {

    private final JwtProvider tokenProvider;
    private final AuthService authService;
    private final OutboxService outboxService;  // 추가

    /**
     * 이메일 인증 이벤트를 Outbox에 저장(발행 예약)
     */
    public void emailVerification(User user) {
        log.info("called emailVerification");
        String token = tokenProvider.createAccessToken(authService.tokenParams(user.getId()));

        outboxService.saveMessage(
                "user-email-verification-topic",
                user.getId(),
                new EmailVerificationEvent(user.getId(), user.getEmail(), token)
        );
    }

    public void userWithdrawal(User user) {
        log.info("called userWithdrawal");

        outboxService.saveMessage(
                "user-withdrawal-topic",
                user.getId(),
                new UserWithdrawEvent(user.getId(), user.getEmail())
        );
    }

    public void userRegistered(User user) {
        log.info("called userRegistered");
        String token = tokenProvider.createAccessToken(authService.tokenParams(user.getId()));

        outboxService.saveMessage(
                "user-registered-topic",
                user.getId(),
                new UserRegisteredEvent(user.getId(), token)
        );
    }
}
