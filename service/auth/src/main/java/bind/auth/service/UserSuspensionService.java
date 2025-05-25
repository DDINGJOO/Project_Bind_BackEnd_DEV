package bind.auth.service;


import bind.auth.dto.request.UserSuspensionRequest;
import bind.auth.dto.response.UserSuspensionStatusResponse;
import bind.auth.entity.UserSuspension;
import bind.auth.repository.UserSuspensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
    public void suspend(UserSuspensionRequest req) {
        // 기존 정지 이력 비활성화 (중복 방지)
        userSuspensionRepository.findByUserIdAndIsActiveTrue(req.userId())
                .ifPresent(prev -> prev.setIsActive(false));

        UserSuspension suspension = UserSuspension.builder()
                .userId(req.userId())
                .reason(req.reason())
                .suspendedAt(LocalDateTime.now())
                .releaseAt(req.releaseAt())
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

    public void liftSuspension(Long id) {
        UserSuspension suspension = userSuspensionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 정지 정보가 없습니다."));

        if (!suspension.isActive()) {
            throw new IllegalArgumentException("이미 해제된 정지입니다.");
        }

        suspension.setIsActive(false);
        userSuspensionRepository.save(suspension);
    }


    public List<UserSuspensionStatusResponse> getActiveSuspendedUsers() {
        List<UserSuspension> activeList = userSuspensionRepository.findByIsActiveTrue();

        return activeList.stream()
                .filter(s -> s.getReleaseAt() == null || s.getReleaseAt().isAfter(LocalDateTime.now()))
                .map(s -> UserSuspensionStatusResponse.builder()
                        .userId(s.getUserId())
                        .reason(s.getReason())
                        .suspendedAt(s.getSuspendedAt())
                        .releaseAt(s.getReleaseAt())
                        .build())
                .collect(Collectors.toList());
    }

    public List<UserSuspensionStatusResponse> getSuspensionHistoryByUser(String userId) {
        List<UserSuspension> suspensions = userSuspensionRepository.findByUserId(userId);

        return suspensions.stream()
                .map(s -> UserSuspensionStatusResponse.builder()
                        .userId(s.getUserId())
                        .isSuspended(s.isActive())
                        .reason(s.getReason())
                        .suspendedAt(s.getSuspendedAt())
                        .releaseAt(s.getReleaseAt())
                        .build())
                .collect(Collectors.toList());
    }
}
