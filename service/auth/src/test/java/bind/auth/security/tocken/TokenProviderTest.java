package bind.auth.security.tocken;// 위치: auth-service/src/test/java/bind/auth/security/TokenProviderTest.java


import bind.auth.config.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenProviderTest {

    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        // 테스트용 임의의 시크릿 키 (32바이트 이상)
        String secret = "test-secret-key-test-secret-key-123456";
        long accessValidity = 1000;      // 1초 (만료 테스트용)
        long refreshValidity = 1000 * 3;     // 1초 (만료 테스트용)

        tokenProvider = new TokenProvider(secret, accessValidity, refreshValidity);
    }

    @Test
    @DisplayName("AccessToken 생성 및 파싱 테스트")
    void createAndParseAccessToken() {
        String userId = UUID.randomUUID().toString();
        String token = tokenProvider.createAccessToken(userId);

        String parsedUserId = String.valueOf(tokenProvider.getUserIdFromToken(token));

        assertThat(parsedUserId).isEqualTo(userId);
        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("RefreshToken 생성 및 파싱 테스트")
    void createAndParseRefreshToken() {
        String userId = UUID.randomUUID().toString();
        String token = tokenProvider.createRefreshToken(userId);

        String parsedUserId = String.valueOf(tokenProvider.getUserIdFromToken(token));

        assertThat(parsedUserId).isEqualTo(userId);
        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("잘못된 토큰 유효성 검사 실패 테스트")
    void invalidTokenShouldFail() {
        String invalidToken = "fake.token.value";
        assertThat(tokenProvider.validateToken(invalidToken)).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 유효성 검사 실패 테스트")
    void expiredTokenShouldFail() throws InterruptedException {
        String userId = UUID.randomUUID().toString();
        String token = tokenProvider.createAccessToken(userId);

        // 1초 기다려 만료시키기
        Thread.sleep(1500);

        assertThat(tokenProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("만료된 RefreshToken 유효성 검사 실패 테스트")
    void expiredRefreshTokenShouldFail() throws InterruptedException {
        String userId = UUID.randomUUID().toString();
        String token = tokenProvider.createRefreshToken(userId);

        // 1초 기다려 만료시키기
        Thread.sleep(3100);

        assertThat(tokenProvider.validateToken(token)).isFalse();
    }
}
