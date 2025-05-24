package bind.auth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "refresh:";

    private String key(String userId, String deviceId) {
        return PREFIX + userId + ":" + deviceId;
    }

    public void save(String userId, String deviceId, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue().set(key(userId, deviceId), refreshToken, ttl);
    }

    public String get(String userId, String deviceId) {
        return redisTemplate.opsForValue().get(key(userId, deviceId));
    }

    public void delete(String userId, String deviceId) {
        redisTemplate.delete(key(userId, deviceId));
    }

    public boolean validate(String userId, String deviceId, String token) {
        String stored = get(userId, deviceId);
        return stored != null && stored.equals(token);
    }
}
