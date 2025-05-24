package bind.auth.service;


import bind.auth.config.TokenProvider;
import bind.auth.dto.request.LoginRequest;
import bind.auth.dto.request.RegisterRequest;
import bind.auth.dto.response.LoginResponse;
import bind.auth.entity.RefreshToken;
import bind.auth.entity.User;
import bind.auth.entity.UserRole;
import bind.auth.exception.AuthException;
import bind.auth.repository.RefreshTokenRepository;
import bind.auth.repository.UserLoginLogRepository;
import bind.auth.repository.UserRepository;
import bind.auth.repository.UserRoleRepository;
import data.enums.auth.ProviderType;
import exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import bind.auth.exception.AuthErrorCode;

import java.time.LocalDateTime;
import java.util.Optional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;
    @Mock private UserLoginLogRepository userLoginLogRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private UserRoleRepository userRoleRepository;
    @Mock private TokenProvider tokenProvider;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RedisService redisService;
    @Mock private UserSuspensionService userSuspensionService;

    private final String userId = "user-1234";
    private final String rawPassword = "password";
    private final String encodedPassword = "encoded-password";
    private final String deviceId = "device-001";
    private final String accessToken = "access-token";
    private final String refreshToken = "refresh-token";

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = User.builder()
                .id(userId)
                .loginId("testId")
                .password(encodedPassword)
                .provider(ProviderType.valueOf("LOCAL"))
                .isActive(true)
                .isSocialOnly(false)
                .build();
    }

    @Test
    @DisplayName("정상 로그인 시 Access/Refresh Token 발급")
    void login_success() {
        // given
        LoginRequest request = new LoginRequest("testId", rawPassword, deviceId);
        when(userRepository.findByLoginId("testId")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(userSuspensionService.isCurrentlySuspended(userId)).thenReturn(false);
        when(tokenProvider.createAccessToken(userId)).thenReturn(accessToken);
        when(tokenProvider.createRefreshToken(userId)).thenReturn(refreshToken);

        // when
        LoginResponse result = authService.login(request, "127.0.0.1", "Chrome");

        // then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);
        verify(redisService).saveRefreshToken(eq(userId), eq(deviceId),eq(refreshToken), any());
    }

    @Test
    @DisplayName("비밀번호 불일치 시 예외 발생")
    void login_passwordMismatch() {
        when(userRepository.findByLoginId("testId")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("testId", rawPassword, deviceId), "1.1.1.1", "Chrome"))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(AuthErrorCode.PASSWORD_NOT_MATCHED.getMessage());
    }

    @Test
    @DisplayName("회원가입 시 기본 권한 부여")
    void register_assignsDefaultRole() {
        when(userRepository.existsByLoginId("newUser")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPwd");

        RegisterRequest request = new RegisterRequest("newUser", "pwd123");
        authService.register(request);

        verify(userRepository).save(any(User.class));
        verify(userRoleRepository).save(any(UserRole.class)); // 권한 저장 검증
    }

    @Test
    @DisplayName("정지된 계정 로그인 차단")
    void login_suspendedUser() {
        when(userRepository.findByLoginId("testId")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(userSuspensionService.isCurrentlySuspended(userId)).thenReturn(true);

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("testId", rawPassword, deviceId), "1.1.1.1", "Chrome"))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(AuthErrorCode.SUSPENDED_USER.getMessage());
    }
    @Test
    @DisplayName("비활성화 계정 로그인 차단")
    void login_deactivatedUser() {
        mockUser.setIsActive(false);
        when(userRepository.findByLoginId("testId")).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("testId", rawPassword, deviceId), "1.1.1.1", "Mozilla"))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.DEACTIVATED_USER.getMessage());
    }

    @Test
    @DisplayName("기존 리프레시 토큰 존재 시 갱신")
    void refreshToken_updateExisting() {
        RefreshToken existing = RefreshToken.builder()
                .userId(userId)
                .deviceId(deviceId)
                .token("old-token")
                .issuedAt(LocalDateTime.now().minusDays(1))
                .expiry(LocalDateTime.now().plusDays(13))
                .build();

        when(refreshTokenRepository.findByUserIdAndDeviceId(userId, deviceId)).thenReturn(Optional.of(existing));
        when(tokenProvider.createRefreshToken(userId)).thenReturn(refreshToken);

        authService.updateRefreshToken(userId, deviceId, "1.1.1.1", "Chrome");

        verify(refreshTokenRepository).save(argThat(rt -> rt.getToken().equals(refreshToken)));
    }
    @Test
    @DisplayName("로그아웃 시 토큰 삭제")
    void logout_success() {
        authService.logout(userId, deviceId);

        verify(redisService).deleteRefreshToken(userId, deviceId);
        verify(refreshTokenRepository).deleteByUserIdAndDeviceId(userId, deviceId);
    }

    @Test
    @DisplayName("유효한 RefreshToken이면 재발급 성공")
    void refreshToken_success() {
        // given
        when(redisService.validateRefreshToken(userId, deviceId, refreshToken)).thenReturn(true);
        when(tokenProvider.createAccessToken(userId)).thenReturn(accessToken);
        when(tokenProvider.createRefreshToken(userId)).thenReturn(refreshToken);

        RefreshToken savedToken = RefreshToken.builder()
                .user(mockUser)
                .token("old-token")
                .deviceId(deviceId)
                .issuedAt(LocalDateTime.now())
                .expiry(LocalDateTime.now().plusDays(14))
                .build();
        when(refreshTokenRepository.findByUserIdAndDeviceId(userId, deviceId)).thenReturn(Optional.of(savedToken));

        // when
        LoginResponse response = authService.refresh(userId, deviceId, refreshToken);

        // then
        assertThat(response.accessToken()).isEqualTo(accessToken);
        assertThat(response.refreshToken()).isEqualTo(refreshToken);
    }


    @Test
    @DisplayName("중복되지 않은 ID로 회원가입 성공")
    void register_success() {
        when(userRepository.existsByLoginId("newUser")).thenReturn(false);
        RegisterRequest req = new RegisterRequest("newUser", "securePwd");

        authService.register(req);

        verify(userRepository).save(any(User.class));
    }
    @Test
    @DisplayName("리프레시 토큰 불일치 시 예외 발생")
    void refreshToken_mismatch_shouldFail() {

        when(redisService.validateRefreshToken(userId, deviceId, refreshToken)).thenReturn(false);

        assertThatThrownBy(() ->
                authService.refresh(userId, deviceId, refreshToken))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage());
    }
    @Test
    @DisplayName("중복된 로그인 ID로 회원가입 시 예외 발생")
    void register_duplicateLoginId() {
        when(userRepository.existsByLoginId("testId")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("testId", "password");
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.DUPLICATE_LOGIN_ID.getMessage());
    }
    @Test
    @DisplayName("RefreshToken이 없는 경우 새로 저장")
    void updateRefreshToken_createNew() {
        when(refreshTokenRepository.findByUserIdAndDeviceId(userId, deviceId)).thenReturn(Optional.empty());
        when(tokenProvider.createRefreshToken(userId)).thenReturn(refreshToken);

        authService.updateRefreshToken(userId, deviceId, "1.1.1.1", "Agent");

        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(redisService).saveRefreshToken(eq(userId),eq(deviceId), eq(refreshToken), any());
    }




}
