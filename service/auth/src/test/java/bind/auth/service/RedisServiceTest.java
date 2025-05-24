package bind.auth.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RedisServiceTest {

    private RedisService redisService;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;

    private final String userId = "user-1";
    private final String deviceId = "device-1";
    private final String token = "refresh-token";

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        redisService = new RedisService(redisTemplate);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    @DisplayName("Refresh 토큰 저장")
    void saveRefreshTokenTest() {
        redisService.saveRefreshToken(userId, deviceId, token, Duration.ofMinutes(30));
        verify(valueOps).set("refresh:user-1:device-1", token, Duration.ofMinutes(30));
    }

    @Test
    @DisplayName("Refresh 토큰 조회")
    void getRefreshTokenTest() {
        when(valueOps.get("refresh:user-1:device-1")).thenReturn(token);
        String result = redisService.getRefreshToken(userId, deviceId);
        assertThat(result).isEqualTo(token);
    }

    @Test
    @DisplayName("Refresh 토큰 삭제")
    void deleteRefreshTokenTest() {
        redisService.deleteRefreshToken(userId, deviceId);
        verify(redisTemplate).delete("refresh:user-1:device-1");
    }

    @Test
    @DisplayName("Refresh 토큰 존재 여부")
    void hasRefreshTokenTest() {
        when(redisTemplate.hasKey("refresh:user-1:device-1")).thenReturn(true);
        boolean result = redisService.hasRefreshToken(userId, deviceId);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Refresh 토큰 유효성 검사")
    void validateRefreshTokenTest() {
        when(valueOps.get("refresh:user-1:device-1")).thenReturn(token);
        boolean isValid = redisService.validateRefreshToken(userId, deviceId, token);
        assertThat(isValid).isTrue();
    }
}
