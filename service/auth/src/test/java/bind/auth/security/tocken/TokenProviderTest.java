package bind.auth.security.tocken; // 위치: auth-service/src/test/java/bind/auth/security/TokenProviderTest.java

import bind.auth.config.TokenProvider;
import bind.auth.exception.AuthErrorCode;
import bind.auth.exception.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TokenProviderTest {

    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        // 테스트용 임의의 시크릿 키 (32바이트 이상)
        String secret = "test-secret-key-test-secret-key-123456";
        long accessValidity = 1000;      // 1초 (만료 테스트용)
        long refreshValidity = 1000 * 3; // 3초 (만료 테스트용)

        tokenProvider = new TokenProvider(secret, accessValidity, refreshValidity);
    }

    @Test
    @DisplayName("AccessToken 생성 및 파싱 테스트")
    void createAndParseAccessToken() {
        String userId = UUID.randomUUID().toString();
        String token = tokenProvider.createAccessToken(userId);

        UUID parsedUserId = tokenProvider.getUserIdFromTokenOrThrow(token);

        assertThat(parsedUserId.toString()).isEqualTo(userId);
    }

    @Test
    @DisplayName("RefreshToken 생성 및 파싱 테스트")
    void createAndParseRefreshToken() {
        String userId = UUID.randomUUID().toString();
        String token = tokenProvider.createRefreshToken(userId);

        UUID parsedUserId = tokenProvider.getUserIdFromTokenOrThrow(token);

        assertThat(parsedUserId.toString()).isEqualTo(userId);
    }

    @Test
    @DisplayName("잘못된 토큰 예외 테스트")
    void invalidTokenShouldThrow() {
        String invalidToken = "fake.token.value";

        assertThatThrownBy(() -> tokenProvider.getUserIdFromTokenOrThrow(invalidToken))
                .isInstanceOf(AuthException.class)
                .extracting(e -> ((AuthException) e).getErrorCode())
                .isEqualTo(AuthErrorCode.TOKEN_INVALID);
    }

    @Test
    @DisplayName("만료된 AccessToken 예외 테스트")
    void expiredAccessTokenShouldThrow() throws InterruptedException {
        String userId = UUID.randomUUID().toString();
        String token = tokenProvider.createAccessToken(userId);

        Thread.sleep(1500);

        assertThatThrownBy(() -> tokenProvider.getUserIdFromTokenOrThrow(token))
                .isInstanceOf(AuthException.class)
                .extracting(e -> ((AuthException) e).getErrorCode())
                .isEqualTo(AuthErrorCode.TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("만료된 RefreshToken 예외 테스트")
    void expiredRefreshTokenShouldThrow() throws InterruptedException {
        String userId = UUID.randomUUID().toString();
        String token = tokenProvider.createRefreshToken(userId);

        Thread.sleep(3100);

        assertThatThrownBy(() -> tokenProvider.getUserIdFromTokenOrThrow(token))
                .isInstanceOf(AuthException.class)
                .extracting(e -> ((AuthException) e).getErrorCode())
                .isEqualTo(AuthErrorCode.TOKEN_EXPIRED);
    }
}
