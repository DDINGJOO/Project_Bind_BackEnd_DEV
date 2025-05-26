package bind.auth.service;


import bind.auth.dto.request.*;
import bind.auth.dto.response.LoginResponse;
import bind.auth.entity.*;
import bind.auth.exception.AuthException;
import bind.auth.repository.*;
import data.enums.auth.ConsentType;
import data.enums.auth.ProviderType;
import data.enums.auth.UserRoleType;
import exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import bind.auth.exception.AuthErrorCode;
import security.jwt.JwtProvider;
import security.jwt.TokenParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks private AuthService authService;

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private UserLoginLogRepository userLoginLogRepository;
    @Mock private UserRoleRepository userRoleRepository;
    @Mock private RedisService redisService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtProvider tokenProvider;
    @Mock private UserSuspensionService userSuspensionService;
    @Mock private PasswordHistoryRepository passwordHistoryRepository;
    @Mock private WithdrawHistoryRepository withdrawHistoryRepository;
    @Mock private ConsentHistoryRepository consentHistoryRepository;
    @Mock private ConsentRequest consentRequest;

    private final String userId = UUID.randomUUID().toString();
    private final String rawPassword = "rawPass123";
    private final String encodedPassword = "encodedPass";
    private final String deviceId = "device001";
    private final String accessToken = "access-token";
    private final String refreshToken = "refresh-token";

    private User mockUser;
    private UserRole mockRole;

    private ConsentRequest mockConsentRequest;


    private final TokenParam tokenParam = TokenParam.builder()
            .userId(userId)
            .role("USER")
            .build();

    @BeforeEach
    void setup() {
        mockUser = User.builder()
                .id(userId)
                .loginId("testUser")
                .password(encodedPassword)
                .isActive(true)
                .provider(ProviderType.LOCAL)
                .build();

        mockRole = UserRole.builder()
                .user(mockUser)
                .role(UserRoleType.USER)
                .build();



        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);


    }

    @Test
    @DisplayName("정상 로그인 시 토큰 발급")
    void login_success() {
        when(userRepository.findByLoginId("testUser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(userSuspensionService.isCurrentlySuspended(userId)).thenReturn(false);
        when(userRoleRepository.findByUserId(userId)).thenReturn(Optional.of(mockRole));
        when(tokenProvider.createAccessToken(tokenParam)).thenReturn(accessToken);
        when(tokenProvider.createRefreshToken(tokenParam)).thenReturn(refreshToken);


        LoginRequest request = new LoginRequest("testUser", rawPassword, deviceId);
        LoginResponse response = authService.login(request, "127.0.0.1", "Chrome");

        assertThat(response.accessToken()).isEqualTo(accessToken);
        assertThat(response.refreshToken()).isEqualTo(refreshToken);
        verify(redisService).saveRefreshToken(eq(userId), eq(deviceId), eq(refreshToken), any(Duration.class));
    }

    @Test
    @DisplayName("비밀번호 불일치 시 예외 발생")
    void login_passwordMismatch() {
        when(userRepository.findByLoginId("testUser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        LoginRequest request = new LoginRequest("testUser", rawPassword, deviceId);

        assertThatThrownBy(() -> authService.login(request, "1.1.1.1", "Chrome"))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.PASSWORD_NOT_MATCHED.getMessage());
    }

    @Test
    @DisplayName("정지된 사용자 로그인 차단")
    void login_suspendedUser() {
        when(userRepository.findByLoginId("testUser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(userSuspensionService.isCurrentlySuspended(userId)).thenReturn(true);

        assertThatThrownBy(() -> authService.login(new LoginRequest("testUser", rawPassword, deviceId), "ip", "agent"))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.SUSPENDED_USER.getMessage());
    }

    @Test
    @DisplayName("비활성 계정 로그인 실패")
    void login_inactiveUser() {
        mockUser.setIsActive(false);
        when(userRepository.findByLoginId("testUser")).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> authService.login(new LoginRequest("testUser", rawPassword, deviceId), "ip", "agent"))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.DEACTIVATED_USER.getMessage());
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        when(userRepository.existsByLoginId("newUser")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPwd");

        RegisterRequest request = new RegisterRequest("newUser", "pass123","temp@mail.com");
        authService.register(request);

        verify(userRepository).save(any(User.class));
        verify(userRoleRepository).save(any(UserRole.class));
    }

    @Test
    @DisplayName("중복 로그인 ID 가입 실패")
    void register_duplicateLoginId() {
        when(userRepository.existsByLoginId("testUser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest("testUser", "1234","teap@mail.com")))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.DUPLICATE_LOGIN_ID.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword_success() {
        // given
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("newPass", "newPass")).thenReturn(true); // newPassword == newPasswordCheck
        when(passwordEncoder.matches("newPass", encodedPassword)).thenReturn(false); // new != old
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");

        PasswordChangeRequest req = new PasswordChangeRequest("newPass", "newPass");

        // when
        authService.changePassword(userId, req);

        // then
        verify(userRepository).save(mockUser);
        verify(passwordHistoryRepository).save(any(PasswordHistory.class));
    }

    @Test
    @DisplayName("비밀번호 확인 불일치 시 예외")
    void changePassword_checkMismatch() {
        PasswordChangeRequest req = new PasswordChangeRequest("newPass1", "newPass2");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("newPass1", "newPass2")).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(userId, req))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.PASSWORD_NOT_MATCHED.getMessage());
    }

    @Test
    @DisplayName("현재 비밀번호와 동일한 경우 예외 발생")
    void changePassword_sameAsOldPassword() {
        PasswordChangeRequest req = new PasswordChangeRequest("samePass", "samePass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("samePass", "samePass")).thenReturn(true);
        when(passwordEncoder.matches("samePass", encodedPassword)).thenReturn(true); // same as current

        assertThatThrownBy(() -> authService.changePassword(userId, req))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.CURRENT_PASSWORD_MATCHED.getMessage());
    }

    @Test
    @DisplayName("유저가 존재하지 않을 경우 예외 발생")
    void changePassword_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        PasswordChangeRequest req = new PasswordChangeRequest("new", "new");

        assertThatThrownBy(() -> authService.changePassword(userId, req))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.USER_NOT_FOUND.getMessage());
    }
    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdraw_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        WithdrawRequest req = new WithdrawRequest("탈퇴 사유");
        authService.withdraw(userId, req);

        verify(userRepository).save(mockUser);
        verify(withdrawHistoryRepository).save(any(WithdrawHistory.class));
    }

    @Test
    @DisplayName("RefreshToken 재발급 성공")
    void refreshToken_success() {
        when(redisService.validateRefreshToken(userId, deviceId, refreshToken)).thenReturn(true);
        when(refreshTokenRepository.findByUserIdAndDeviceId(userId, deviceId)).thenReturn(
                Optional.of(RefreshToken.builder().token("old").build()));
        when(userRoleRepository.findByUserId(userId)).thenReturn(Optional.of(mockRole));
        when(tokenProvider.createAccessToken(tokenParam)).thenReturn(accessToken);
        when(tokenProvider.createRefreshToken(tokenParam)).thenReturn(refreshToken);

        LoginResponse response = authService.refresh(userId, deviceId, refreshToken);

        assertThat(response.accessToken()).isEqualTo(accessToken);
        assertThat(response.refreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("RefreshToken 검증 실패 시 예외")
    void refreshToken_invalid() {
        when(redisService.validateRefreshToken(userId, deviceId, refreshToken)).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh(userId, deviceId, refreshToken))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage());
    }

    @Test
    @DisplayName("로그아웃 시 리프레시 토큰 제거")
    void logout_success() {
        authService.logout(userId, deviceId);

        verify(redisService).deleteRefreshToken(userId, deviceId);
        verify(refreshTokenRepository).deleteByUserIdAndDeviceId(userId, deviceId);
    }
}
