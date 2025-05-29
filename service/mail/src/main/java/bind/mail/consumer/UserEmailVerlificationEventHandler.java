package bind.mail.consumer;

import bind.mail.service.MailService;
import event.constant.EventType;
import event.dto.EmailVerificationEvent;
import event.dto.UserRegisteredEvent;
import event.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEmailVerlificationEventHandler implements EventHandler<EmailVerificationEvent> {


    private final MailService mailService;

    @Override
    public EventType supportedType() {
        return EventType.USER_REGISTERED;
    }

    @Override
    public void handle(EmailVerificationEvent event) {
        // 실제 사용자 등록 후 처리할 비즈니스 로직 수행
        System.out.println("새로운 사용자가 등록되었습니다: " + event.getReferenceId());
        // 추가 로직 (예: 이메일 발송 요청 등)
        // 예시: 이메일 발송 로직
        mailService.sendVerificationEmail(
                event
        );


    }
}
