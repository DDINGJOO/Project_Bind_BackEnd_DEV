package bind.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    private String getRefreshKey(String userId, String deviceId) {
        return "refresh:" + userId + ":" + deviceId;
    }


    public void saveRefreshToken(String userId, String deviceId, String token, Duration ttl) {
        redisTemplate.opsForValue().set(getRefreshKey(userId, deviceId), token, ttl);
    }


    public String getRefreshToken(String userId, String deviceId) {
        return redisTemplate.opsForValue().get(getRefreshKey(userId, deviceId));
    }


    public void deleteRefreshToken(String userId, String deviceId) {
        redisTemplate.delete(getRefreshKey(userId, deviceId));
    }


    public boolean hasRefreshToken(String userId, String deviceId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(getRefreshKey(userId, deviceId)));
    }


    public boolean validateRefreshToken(String userId, String deviceId, String token) {
        String stored = getRefreshToken(userId, deviceId);
        return token.equals(stored);
    }
}
