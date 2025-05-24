package bind.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SocialLoginRequest(
        @NotBlank(message = "소셜 로그인 공급자(provider)는 필수입니다")
        String provider,  // e.g., GOOGLE, KAKAO

        @NotBlank(message = "소셜 인증 코드(code)는 필수입니다")
        String code,  // authorization code from OAuth provider

        String deviceId
) {}
