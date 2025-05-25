package bind.auth.service;


import bind.auth.dto.request.UserSuspensionRequest;
import bind.auth.dto.response.UserSuspensionStatusResponse;
import bind.auth.entity.UserSuspension;
import bind.auth.repository.UserSuspensionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSuspensionServiceTest {

    @InjectMocks
    private UserSuspensionService userSuspensionService;

    @Mock
    private UserSuspensionRepository userSuspensionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("현재 정지 여부 확인 - 정지 상태일 경우 true")
    void isCurrentlySuspended_true() {
        String userId = "user123";
        UserSuspension suspension = UserSuspension.builder()
                .userId(userId)
                .isActive(true)
                .releaseAt(LocalDateTime.now().plusDays(1))
                .build();

        when(userSuspensionRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(suspension));

        boolean result = userSuspensionService.isCurrentlySuspended(userId);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("정지 등록")
    void suspend_registersNewSuspension() {
        String userId = "user123";
        UserSuspensionRequest request = new UserSuspensionRequest(userId, "사유", LocalDateTime.now().plusDays(3));

        when(userSuspensionRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());

        userSuspensionService.suspend(request);

        ArgumentCaptor<UserSuspension> captor = ArgumentCaptor.forClass(UserSuspension.class);
        verify(userSuspensionRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("정지 자동 해제 - 해제 조건 충족")
    void releaseIfExpired_success() {
        String userId = "user123";
        UserSuspension suspension = UserSuspension.builder()
                .userId(userId)
                .isActive(true)
                .releaseAt(LocalDateTime.now().minusHours(1))
                .build();

        when(userSuspensionRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(suspension));

        userSuspensionService.releaseIfExpired(userId);

        assertThat(suspension.isActive()).isFalse();
    }

    @Test
    @DisplayName("정지 직접 해제 - 정상 동작")
    void liftSuspension_success() {
        Long id = 1L;
        UserSuspension suspension = UserSuspension.builder()
                .id(id)
                .isActive(true)
                .build();

        when(userSuspensionRepository.findById(id)).thenReturn(Optional.of(suspension));

        userSuspensionService.liftSuspension(id);

        assertThat(suspension.isActive()).isFalse();
    }

    @Test
    @DisplayName("정지 이력 목록 확인")
    void getSuspensionHistoryByUser() {
        String userId = "user123";
        UserSuspension s1 = UserSuspension.builder()
                .userId(userId)
                .reason("욕설")
                .isActive(false)
                .suspendedAt(LocalDateTime.now().minusDays(3))
                .releaseAt(LocalDateTime.now().minusDays(1))
                .build();
        when(userSuspensionRepository.findByUserId(userId)).thenReturn(List.of(s1));

        List<UserSuspensionStatusResponse> history = userSuspensionService.getSuspensionHistoryByUser(userId);

        assertThat(history).hasSize(1);
        assertThat(history.get(0).reason()).isEqualTo("욕설");
    }

    @Test
    @DisplayName("정지된 사용자 목록 반환")
    void getActiveSuspendedUsers() {
        UserSuspension s1 = UserSuspension.builder()
                .userId("user2")
                .isActive(true)
                .reason("도배")
                .releaseAt(LocalDateTime.now().plusDays(1))
                .suspendedAt(LocalDateTime.now())
                .build();

        when(userSuspensionRepository.findByIsActiveTrue()).thenReturn(List.of(s1));

        List<UserSuspensionStatusResponse> result = userSuspensionService.getActiveSuspendedUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo("user2");
    }
}
