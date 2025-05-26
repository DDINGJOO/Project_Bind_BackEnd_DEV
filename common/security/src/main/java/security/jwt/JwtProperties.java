package security.jwt;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret = "test-secret-key-test-secret-key-123456"; // JWT 서명에 사용되는 비밀 키
    private long accessTokenValidity = 1320000; // 3시간
    private long refreshTokenValidity = 604800000; // 7일
}
