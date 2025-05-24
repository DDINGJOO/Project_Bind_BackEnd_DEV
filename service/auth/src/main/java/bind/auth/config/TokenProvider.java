package bind.auth.config; // 위치: auth-service/src/main/java/bind/auth/security/TokenProvider.java

import bind.auth.exception.AuthErrorCode;
import bind.auth.exception.AuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class TokenProvider {

    private final Key key;
    private final long accessTokenValidityInMs;
    private final long refreshTokenValidityInMs;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-ms}") long accessTokenValidityInMs,
            @Value("${jwt.refresh-token-validity-ms}") long refreshTokenValidityInMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidityInMs = accessTokenValidityInMs;
        this.refreshTokenValidityInMs = refreshTokenValidityInMs;
    }

    public String createAccessToken(String userId) {
        return createToken(userId, accessTokenValidityInMs);
    }

    public String createRefreshToken(String userId) {
        return createToken(userId, refreshTokenValidityInMs);
    }

    private String createToken(String userId, long validityInMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID getUserIdFromTokenOrThrow(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return UUID.fromString(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED.getMessage(), AuthErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID.getMessage(), AuthErrorCode.TOKEN_INVALID);
        }
    }

    public void validateTokenOrThrow(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED.getMessage(), AuthErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID.getMessage(), AuthErrorCode.TOKEN_INVALID);
        }
    }

    public Claims decodeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
