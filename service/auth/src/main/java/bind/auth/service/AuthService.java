package bind.auth.service;



import bind.auth.dto.request.*;
import bind.auth.dto.response.LoginResponse;
import bind.auth.entity.*;
import bind.auth.exception.AuthErrorCode;
import bind.auth.exception.AuthException;

import bind.auth.repository.*;



import data.enums.auth.ProviderType;
import data.enums.auth.UserRoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import security.jwt.JwtProvider;
import security.jwt.TokenParam;

import util.PkProvider;


import java.time.Duration;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserLoginLogRepository userLoginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private  final JwtProvider tokenProvider;
    private final UserRoleRepository userRoleRepository;


    private final UserSuspensionService userSuspensionService;
    private final WithdrawHistoryRepository withdrawHistoryRepository;
    private final RedisService redisService;
    private final PasswordHistoryRepository passwordHistoryRepository;





    public LoginResponse login(LoginRequest request, String ip, String userAgent) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND.getMessage(), AuthErrorCode.USER_NOT_FOUND));

        AuthErrorCode errorCode = vaildateRegister(user,request);
        if (errorCode != null) {
            logLoginAttempt(user, ip, userAgent, request.deviceId(), false, errorCode.getMessage());
            throw new AuthException(errorCode.getMessage(), errorCode);
        }

        logLoginAttempt(user, ip, userAgent, request.deviceId(),true, "Login successful");
        user.setLoginFailCount(0);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return generateTokens(user,request);
    }

    public void logout(String userId, String deviceId) {
        redisService.deleteRefreshToken(userId, deviceId);
        refreshTokenRepository.deleteByUserIdAndDeviceId(userId, deviceId);
    }

    @Transactional
    public void changePassword(String loginId, PasswordChangeRequest request) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND.getMessage(), AuthErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.newPassword(), request.newPasswordCheck())) {
            throw new AuthException(AuthErrorCode.PASSWORD_NOT_MATCHED.getMessage(), AuthErrorCode.PASSWORD_NOT_MATCHED);
        }

        if(passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new AuthException(AuthErrorCode.CURRENT_PASSWORD_MATCHED.getMessage(), AuthErrorCode.CURRENT_PASSWORD_MATCHED);
        }

        String encodedNewPassword = passwordEncoder.encode(request.newPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        passwordHistoryRepository.save(
                PasswordHistory.builder()
                        .user(user)
                        .passwordHash(encodedNewPassword)
                        .changedAt(LocalDateTime.now())
                        .build()
        );
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new AuthException(AuthErrorCode.DUPLICATE_LOGIN_ID.getMessage(), AuthErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException(AuthErrorCode.DUPLICATE_EMAIL.getMessage(), AuthErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .id(PkProvider.getInstance().generate())
                .loginId(request.loginId())
                .email(request.email())
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
        return user;

    }






    @Transactional
    public LoginResponse refresh(String userId, String deviceId, String refreshToken) {
        if (!redisService.validateRefreshToken(userId, deviceId, refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage(), AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        TokenParam param = tokenParams(userId);
        String newAccessToken = tokenProvider.createAccessToken(param);
        String newRefreshToken = tokenProvider.createRefreshToken(param);

        redisService.saveRefreshToken(userId, deviceId, newRefreshToken, Duration.ofDays(14));

        RefreshToken tokenEntity = refreshTokenRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage(), AuthErrorCode.INVALID_REFRESH_TOKEN));

        tokenEntity.update(newRefreshToken, LocalDateTime.now(), LocalDateTime.now().plusDays(14));
        refreshTokenRepository.save(tokenEntity);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }



    private void logLoginAttempt(User user, String ip, String userAgent,String deviceId, boolean success, String reason) {
        UserLoginLog log = UserLoginLog.builder()
                .user(user)
                .ipAddress(ip)
                .userAgent(userAgent)
                .reason(reason)
                .deviceId(deviceId)
                .success(success)
                .loginAt(LocalDateTime.now())
                .build();
        userLoginLogRepository.save(log);
    }



    private AuthErrorCode vaildateRegister(User user,LoginRequest request)
    {
        if(!user.isEmailVerified()){
            return  AuthErrorCode.EMAIL_NOT_VERIFIED;
        }


        if (!user.isActive()) {
            return AuthErrorCode.DEACTIVATED_USER;
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return AuthErrorCode.PASSWORD_NOT_MATCHED;
        }

        if (userSuspensionService.isCurrentlySuspended(user.getId())) {
            return AuthErrorCode.SUSPENDED_USER;
        }

        return null;

    }


    public User withdraw(String userId, WithdrawRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND.getMessage(), AuthErrorCode.USER_NOT_FOUND));
        user.setIsActive(false);
        userRepository.save(user);

        withdrawHistoryRepository.save(WithdrawHistory.builder()
                .user(user)
                .reason(request.reason())
                .withdrawAt(LocalDateTime.now())
                .build());
        return user;
    }

    public TokenParam tokenParams(String userId) {
        UserRole role = userRoleRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_ROLE_NOT_FOUND.getMessage(), AuthErrorCode.USER_ROLE_NOT_FOUND));
        return new TokenParam(userId, role.getRole().name());
    }

    public Page<UserLoginLog> getLoginLogs(Pageable pageable) {
        return userLoginLogRepository.findAll(pageable);
    }

    private LoginResponse generateTokens(User user, LoginRequest request) {
        TokenParam param = tokenParams(user.getId());
        String accessToken = tokenProvider.createAccessToken(param);
        String refreshToken = tokenProvider.createRefreshToken(param);

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

        return new LoginResponse(accessToken, refreshToken);
    }


    @Transactional
    public User confirmEmail(String token) {
        String userId = tokenProvider.getUserIdFromToken(token);
        System.out.println("User ID from token: " + userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND.getMessage(), AuthErrorCode.USER_NOT_FOUND));

        if(user.isEmailVerified()) {
            throw new AuthException(AuthErrorCode.ALREADY_VERIFIED.getMessage(), AuthErrorCode.ALREADY_VERIFIED);
        }
        user.setIsEmailVerified(true);
        userRepository.save(user);

        return user;
    }
}
