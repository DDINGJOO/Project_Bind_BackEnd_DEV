package bind.auth.service;



import bind.auth.config.TokenProvider;
import bind.auth.dto.request.LoginRequest;
import bind.auth.dto.request.RegisterRequest;
import bind.auth.dto.response.LoginResponse;
import bind.auth.entity.*;
import bind.auth.exception.AuthErrorCode;
import bind.auth.exception.AuthException;

import bind.auth.repository.RefreshTokenRepository;
import bind.auth.repository.UserLoginLogRepository;
import bind.auth.repository.UserRepository;

import bind.auth.repository.UserRoleRepository;
import data.enums.auth.ProviderType;
import data.enums.auth.UserRoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.PkProvider;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserLoginLogRepository userLoginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final TokenProvider tokenProvider;
    private final UserSuspensionService userSuspensionService;
    private final RedisService redisService;


    public LoginResponse login(LoginRequest request, String ip, String userAgent) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND.getMessage(), AuthErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new AuthException(AuthErrorCode.DEACTIVATED_USER.getMessage(), AuthErrorCode.DEACTIVATED_USER);
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            logLoginAttempt(user, ip, userAgent, false);
            throw new AuthException(AuthErrorCode.PASSWORD_NOT_MATCHED.getMessage(), AuthErrorCode.PASSWORD_NOT_MATCHED);
        }

        if (userSuspensionService.isCurrentlySuspended(user.getId())) {
            throw new AuthException(AuthErrorCode.SUSPENDED_USER.getMessage(), AuthErrorCode.SUSPENDED_USER);
        }

        String accessToken = tokenProvider.createAccessToken(user.getId());
        String refreshToken = tokenProvider.createRefreshToken(user.getId());

        // Redis 저장
        redisService.saveRefreshToken(user.getId(), request.deviceId(), refreshToken, Duration.ofDays(14));

        // DB 저장
        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .deviceId(request.deviceId())
                .token(refreshToken)
                .issuedAt(LocalDateTime.now())
                .expiry(LocalDateTime.now().plusDays(14))
                .build();
        refreshTokenRepository.save(entity);

        logLoginAttempt(user, ip, userAgent, true);

        return new LoginResponse(accessToken, refreshToken);
    }

    public void logout(String userId, String deviceId) {
        redisService.deleteRefreshToken(userId, deviceId);
        refreshTokenRepository.deleteByUserIdAndDeviceId(userId, deviceId);
    }


    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new AuthException(AuthErrorCode.DUPLICATE_LOGIN_ID.getMessage(), AuthErrorCode.DUPLICATE_LOGIN_ID);
        }

        User user = User.builder()
                .id(PkProvider.getInstance().generate())
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .provider(ProviderType.valueOf("LOCAL"))
                .isSocialOnly(false)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        UserRole role = new UserRole(
                user,
                new UserRoleId(user.getId(), UserRoleType.USER).getRole(),
                LocalDateTime.now(),
                "System"
        );
        userRoleRepository.save(role);
    }

    public LoginResponse refresh(String userId, String deviceId, String refreshToken) {
        if (!redisService.validateRefreshToken(userId, deviceId, refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage(), AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = tokenProvider.createAccessToken(userId);
        String newRefreshToken = tokenProvider.createRefreshToken(userId);

        redisService.saveRefreshToken(userId, deviceId, newRefreshToken, Duration.ofDays(14));

        RefreshToken tokenEntity = refreshTokenRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage(), AuthErrorCode.INVALID_REFRESH_TOKEN));

        tokenEntity.update(newRefreshToken, LocalDateTime.now(), LocalDateTime.now().plusDays(14));
        refreshTokenRepository.save(tokenEntity);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    private void logLoginAttempt(User user, String ip, String userAgent, boolean success) {
        UserLoginLog log = UserLoginLog.builder()
                .user(user)
                .ipAddress(ip)
                .userAgent(userAgent)
                .success(success)
                .loginAt(LocalDateTime.now())
                .build();
        userLoginLogRepository.save(log);
    }
    public void updateRefreshToken(String userId, String deviceId, String ip, String userAgent) {
        String newRefreshToken = tokenProvider.createRefreshToken(userId);

        // DB 갱신
        Optional<RefreshToken> existing = refreshTokenRepository.findByUserIdAndDeviceId(userId, deviceId);
        if (existing.isPresent()) {
            existing.get().update(newRefreshToken, LocalDateTime.now(), LocalDateTime.now().plusDays(14));
            refreshTokenRepository.save(existing.get());
        } else {
            RefreshToken tokenEntity = RefreshToken.builder()
                    .userId(userId)
                    .deviceId(deviceId)
                    .clientIp(ip)
                    .userAgent(userAgent)
                    .token(newRefreshToken)
                    .issuedAt(LocalDateTime.now())
                    .expiry(LocalDateTime.now().plusDays(14))
                    .build();
            refreshTokenRepository.save(tokenEntity);
        }

        // Redis 저장
        redisService.saveRefreshToken(userId, deviceId,newRefreshToken, Duration.ofDays(14));
    }

}
