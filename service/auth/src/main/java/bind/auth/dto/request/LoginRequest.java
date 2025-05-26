package bind.auth.dto.request;


import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record LoginRequest(

        @NotBlank(message = "로그인 아이디는 필수입니다.")
        String loginId,
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,
        String deviceId
) {}
