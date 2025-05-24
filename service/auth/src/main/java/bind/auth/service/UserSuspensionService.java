package bind.auth.service;


import bind.auth.entity.UserSuspension;
import bind.auth.repository.UserSuspensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserSuspensionService {

    private final UserSuspensionRepository userSuspensionRepository;

    /**
     * 현재 정지 상태인지 검사
     */
    @Transactional(readOnly = true)
    public boolean isCurrentlySuspended(String userId) {
        Optional<UserSuspension> suspension = userSuspensionRepository.findByUserIdAndIsActiveTrue(userId);

        return suspension
                .filter(s -> s.getReleaseAt() == null || s.getReleaseAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    /**
     * 정지 등록
     */
    @Transactional
    public void suspend(String userId, String reason, LocalDateTime releaseAt) {
        // 기존 정지 이력 비활성화 (중복 방지)
        userSuspensionRepository.findByUserIdAndIsActiveTrue(userId)
                .ifPresent(prev -> prev.setIsActive(false));

        UserSuspension suspension = UserSuspension.builder()
                .userId(userId)
                .reason(reason)
                .suspendedAt(LocalDateTime.now())
                .releaseAt(releaseAt)
                .isActive(true)
                .build();

        userSuspensionRepository.save(suspension);
    }

    /**
     * 정지 만료 처리 (스케줄러 등에서 사용 가능)
     */
    @Transactional
    public void releaseIfExpired(String userId) {
        userSuspensionRepository.findByUserIdAndIsActiveTrue(userId)
                .filter(s -> s.getReleaseAt() != null && s.getReleaseAt().isBefore(LocalDateTime.now()))
                .ifPresent(s -> s.setIsActive(false));
    }

    @Transactional
    public int releaseExpiredSuspensions() {
        LocalDateTime now = LocalDateTime.now();
        var expired = userSuspensionRepository.findAll().stream()
                .filter(s -> s.isActive() && s.getReleaseAt() != null && s.getReleaseAt().isBefore(now))
                .toList();

        expired.forEach(s -> s.setIsActive(false));

        return expired.size();
    }
}
