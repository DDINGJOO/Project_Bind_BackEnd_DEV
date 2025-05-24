package bind.auth.dto.response;


public record SocialLoginResponse(
        String userId,
        String accessToken,
        String refreshToken,
        String loginId,
        String provider,
        boolean isNewUser
) {}
