package bind.mail.dto;


import lombok.Builder;

@Builder
public record MailTxtSendDto(
        String emailAddr,
        String subject,
        String content
) {


}
