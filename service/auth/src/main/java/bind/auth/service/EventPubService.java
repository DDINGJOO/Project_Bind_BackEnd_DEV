package bind.auth.service;

import bind.auth.entity.User;


import event.dto.EmailVerificationEvent;
import event.dto.UserRegisteredEvent;
import event.dto.UserWithdrawEvent;
import event.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import security.jwt.JwtProvider;


@Service
@RequiredArgsConstructor
@Slf4j

public class EventPubService {

    private  final JwtProvider tokenProvider;
    private final EventProducer eventProducer;
    private final AuthService authService;

    /**
     * 이메일 인증 이벤트를 카프카에 발행합니다.
     *
     * @param user 인증이 필요한 사용자 정보
     */
    public void kafkaEmailVerification(User user)
    {
        log.info("called kafkaEmailVerification");
        String token = tokenProvider.createAccessToken(authService.tokenParams(user.getId()));


        eventProducer.publishEvent("user-email-verification-topic",
                new EmailVerificationEvent(user.getId(), user.getEmail(),token)
        );
    }



    public void kafkaUserWithdrawal(User user) {
        log.info("called kafkaUserWithdrawal");
        eventProducer.publishEvent("user-withdrawal-topic",
                new UserWithdrawEvent(user.getEmail()));
    }


    public void kafkaUserRegistered(User user) {
        log.info("called kafkaUserRegistered");
        String token = tokenProvider.createAccessToken(authService.tokenParams(user.getId()));

        eventProducer.publishEvent("user-registered-topic",
                new UserRegisteredEvent(user.getId(), token));
    }
}
