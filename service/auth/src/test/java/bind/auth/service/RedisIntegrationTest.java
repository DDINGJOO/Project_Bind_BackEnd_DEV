package bind.auth.service;


import bind.auth.config.TokenProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RedisIntegrationTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private TokenProvider tokenProvider;

    private final String userId = "test-user-123";
    private final String deviceId = "device-001";

    @Test
    void saveAndDecodeRefreshToken() {
        // 토큰 생성 및 저장
        String token = tokenProvider.createRefreshToken(userId);
        redisService.saveRefreshToken(userId, deviceId, token, Duration.ofMinutes(10));

        // Redis에서 다시 가져옴
        String storedToken = redisService.getRefreshToken(userId, deviceId);
        System.out.println("Stored Token = " + storedToken);

        // 디코딩해서 Claims 확인
        Claims claims = tokenProvider.decodeToken(storedToken);
        System.out.println("Claims subject (userId) = " + claims.getSubject());
        System.out.println("Claims issuedAt = " + claims.getIssuedAt());
        System.out.println("Claims expiration = " + claims.getExpiration());

        assertThat(claims.getSubject()).isEqualTo(userId);
    }
}

