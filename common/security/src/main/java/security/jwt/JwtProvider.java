package security.jwt;




import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import security.exception.SecurityErrorCode;


import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String createAccessToken(TokenParam param) {
        return createToken(param, jwtProperties.getAccessTokenValidity());
    }

    public String createRefreshToken(TokenParam param) {
        return createToken(param, jwtProperties.getRefreshTokenValidity());
    }

    private String createToken(TokenParam param, long validityMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMs);

        return Jwts.builder()
                .setSubject(param.userId())
                .claim("role", param.role())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String getUserIdFromToken(String token) {
        try {
            // "Bearer " 접두사를 제거하고 토큰을 디코딩합니다.
            token = pasteToken(token);
            Claims claims = decodeToken(token);
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT parsing failed: {}", e.getMessage());
            throw new security.exception.SecurityException(SecurityErrorCode.TOKEN_INVALID.getMessage(), SecurityErrorCode.TOKEN_INVALID);
        }
    }

    public String getRoleFromToken(String token) {
        try {
            // "Bearer " 접두사를 제거하고 토큰을 디코딩합니다.
            token = pasteToken(token);
            Claims claims = decodeToken(token);
            return claims.get("role", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT parsing failed: {}", e.getMessage());
            throw new security.exception.SecurityException(SecurityErrorCode.TOKEN_INVALID.getMessage(), SecurityErrorCode.TOKEN_INVALID);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    /**
     * 토큰을 디코딩하여 Claims 객체를 반환합니다.
     * 만약 토큰이 만료되었거나 유효하지 않으면 예외를 발생시킵니다.
     *
     * @param storedToken 디코딩할 JWT 토큰
     * @return Claims 객체
     */
    public Claims decodeToken(String storedToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(storedToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new security.exception.SecurityException(SecurityErrorCode.TOKEN_EXPIRED.getMessage(), SecurityErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new security.exception.SecurityException(SecurityErrorCode.TOKEN_INVALID.getMessage(), SecurityErrorCode.TOKEN_INVALID);
        }
    }

    private String pasteToken(String token) {
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        } else {
            return token;
        }
    }
}
