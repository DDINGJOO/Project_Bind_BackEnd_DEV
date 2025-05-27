package bind.auth.service;

import bind.auth.entity.User;


import event.constant.EventType;
import event.events.Event;
import event.payload.EmailVerificationEventPayload;
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


        Event<EmailVerificationEventPayload> event = Event.<EmailVerificationEventPayload>builder()
                .type(EventType.EMAIL_VERIFICATION)
                .payload(EmailVerificationEventPayload.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .verificationToken(token)
                        .build())
                .timestamp(System.currentTimeMillis())
                .build();

        eventProducer.send(event.getType(), event.getPayload(), "mails-topic");
    }
}
