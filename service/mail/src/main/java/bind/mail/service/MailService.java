package bind.mail.service;





import event.dto.EmailVerificationEventPayload;
import event.dto.UserWithdrawEventPayload;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(EmailVerificationEventPayload payload) {

        //TODO : MAIL PATH  CHECK
        System.out.println("This is MailService.sendVerificationEmail() \n email : " + payload.getEmail() + "\n token :" + payload.getToken());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(payload.getEmail());
            helper.setSubject("[BANDER] 회원가입 이메일 인증");
            helper.setText("<h1>이메일 인증</h1>" +
                            "<p>아래 링크를 클릭하여 이메일을 인증해주세요:</p>" +
                            "<a href='http://localhost:9000/api/auth/verify-email?token=" + payload.getToken() + "'>이메일 인증하기</a>",
                    true);

            mailSender.send(message);
            log.info("이메일 전송 성공: {}", payload.getEmail());

        } catch (MessagingException e) {
            log.error("이메일 전송 실패", e);
        }
    }



    public void sendGoodByeMail(UserWithdrawEventPayload event)
    {
        //TODO : REMOVE THIS LINE
        System.out.println("This is MailService.sendGoodByeMail() \n email : " + event.getEmail());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(event.getEmail());
            helper.setSubject("[BANDER] 회원 탈퇴 안내");
            helper.setText("<h1>회원 탈퇴 안내</h1>" +
                            "<p>회원님의 탈퇴가 완료되었습니다. 그동안 이용해 주셔서 감사합니다.</p>",
                    true);

            mailSender.send(message);
            log.info("탈퇴 안내 이메일 전송 성공: {}", event.getEmail());

        } catch (MessagingException e) {
            log.error("탈퇴 안내 이메일 전송 실패", e);
        }
    }



}




