package bind.auth.service;

import bind.auth.repository.UserSuspensionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserSuspensionServiceTest {

    @Autowired
    private UserSuspensionService userSuspensionService;

    @Autowired
    private UserSuspensionRepository userSuspensionRepository;

    @Test
    @DisplayName("사용자 정지를 등록하고 현재 정지 상태로 인식되는지 확인한다")
    void suspendUser_and_check() {
        // given
        String userId = "user-123";
        String reason = "욕설";
        LocalDateTime releaseAt = LocalDateTime.now().plusHours(1);

        // when
        userSuspensionService.suspend(userId, reason, releaseAt);

        // then
        assertTrue(userSuspensionService.isCurrentlySuspended(userId));
    }

    @Test
    @DisplayName("정지 만료 후 자동 해제되는지 확인한다")
    void expiredSuspension_shouldBeReleased() {
        // given
        String userId = "user-456";
        userSuspensionService.suspend(userId, "테스트", LocalDateTime.now().minusMinutes(1));

        // when
        int count = userSuspensionService.releaseExpiredSuspensions();

        // then
        assertEquals(1, count);
        assertFalse(userSuspensionService.isCurrentlySuspended(userId));
    }
}
