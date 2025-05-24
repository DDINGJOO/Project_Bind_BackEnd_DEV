package bind.auth.security.tocken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import security.jwt.JwtProperties;
import security.jwt.JwtProvider;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TokenProviderTest {

    private JwtProvider tokenProvider;

    @BeforeEach
    void setUp() {
        // 수동으로 JwtProperties 설정
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-test-secret-key-123456");
        jwtProperties.setAccessTokenValidity(1000);     // 1초
        jwtProperties.setRefreshTokenValidity(3000);    // 3초

        tokenProvider = new JwtProvider(jwtProperties);
        tokenProvider.init(); // PostConstruct 직접 호출
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
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    @DisplayName("만료된 AccessToken 예외 테스트")
    void expiredAccessTokenShouldThrow() throws InterruptedException {
        String userId = UUID.randomUUID().toString();
        String token = tokenProvider.createAccessToken(userId);

        Thread.sleep(1500); // 1.5초 후 만료됨

        assertThatThrownBy(() -> tokenProvider.getUserIdFromTokenOrThrow(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    @DisplayName("만료된 RefreshToken 예외 테스트")
    void expiredRefreshTokenShouldThrow() throws InterruptedException {
        String userId = UUID.randomUUID().toString();
        String token = tokenProvider.createRefreshToken(userId);

        Thread.sleep(3100); // 3.1초 후 만료됨

        assertThatThrownBy(() -> tokenProvider.getUserIdFromTokenOrThrow(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid token");
    }
}
