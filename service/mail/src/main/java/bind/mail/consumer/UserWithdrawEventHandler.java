package bind.mail.consumer;

import bind.mail.service.MailService;
import event.constant.EventType;

import event.dto.UserWithdrawEvent;
import event.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserWithdrawEventHandler  implements EventHandler<UserWithdrawEvent> {

    private final MailService mailService;

    @Override
    public EventType supportedType() {
        return EventType.USER_WITHDRAWN;
    }

    @Override
    public void handle(UserWithdrawEvent event) {
        // 실제 사용자 등록 후 처리할 비즈니스 로직 수행
        System.out.println("사용자가 탈퇴 하였습니다. : " + event.getEmail());
        // 추가 로직 (예: 이메일 발송 요청 등)
        // 예시: 이메일 발송 로직
        mailService.sendGoodByeMail(
                event
        );


    }
}
