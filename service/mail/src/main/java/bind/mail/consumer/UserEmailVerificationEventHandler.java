package bind.mail.consumer;

import bind.mail.service.MailService;
import event.constant.EventType;

import event.domain.Event;
import event.dto.EmailVerificationEventPayload;
import event.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
