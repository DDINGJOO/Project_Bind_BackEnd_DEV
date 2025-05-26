package bind.auth.dto.request;

import data.enums.auth.ConsentType;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RegisterRequest(
        @NotBlank(message = "로그인 ID는 필수입니다")
        String loginId,
        @NotBlank(message = "비밀번호는 필수입니다")
        String password,
        @NotBlank(message = "이메일은 필수 입니다.")
        String email
) {}
