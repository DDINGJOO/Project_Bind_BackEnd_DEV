package bind.auth.security.tocken;

import bind.auth.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import security.jwt.JwtProperties;
import security.jwt.JwtProvider;
import security.jwt.TokenParam;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class TokenProviderTest {

    private JwtProvider tokenProvider;

    @BeforeEach
    void setUp() {
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
        TokenParam param = new TokenParam(UUID.randomUUID().toString(), "USER");
        String token = tokenProvider.createAccessToken(param);

        String parsedUserId = tokenProvider.getUserIdFromToken(token);
        assertThat(parsedUserId).isEqualTo(param.userId());
    }

    @Test
    @DisplayName("RefreshToken 생성 및 파싱 테스트")
    void createAndParseRefreshToken() {
        TokenParam param = new TokenParam(UUID.randomUUID().toString(), "USER");
        String token = tokenProvider.createRefreshToken(param);

        String parsedUserId = tokenProvider.getUserIdFromToken(token);
        assertThat(parsedUserId).isEqualTo(param.userId());
    }

    @Test
    @DisplayName("잘못된 토큰 예외 테스트")
    void invalidTokenShouldThrow() {
        String invalidToken = "fake.token.value";

        assertThatThrownBy(() -> tokenProvider.getUserIdFromToken(invalidToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("유효하지 않은 토큰입니다.");
    }

    @Test
    @DisplayName("만료된 AccessToken 예외 테스트")
    void expiredAccessTokenShouldThrow() throws InterruptedException {
        TokenParam param = new TokenParam(UUID.randomUUID().toString(), "USER");
        String token = tokenProvider.createAccessToken(param);

        Thread.sleep(1500); // wait for expiration

        assertThatThrownBy(() -> tokenProvider.getUserIdFromToken(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("토큰이 만료되었습니다.");
    }

    @Test
    @DisplayName("만료된 RefreshToken 예외 테스트")
    void expiredRefreshTokenShouldThrow() throws InterruptedException {
        TokenParam param = new TokenParam(UUID.randomUUID().toString(), "USER");
        String token = tokenProvider.createRefreshToken(param);

        Thread.sleep(3100); // wait for expiration

        assertThatThrownBy(() -> tokenProvider.getUserIdFromToken(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("토큰이 만료되었습니다.");
    }
}
