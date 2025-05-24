package bind.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


public record PasswordChangeRequest (

    @NotBlank(message = "비밀번호는 필수입니다.")
    String newPassword,

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    String newPasswordCheck
){}
