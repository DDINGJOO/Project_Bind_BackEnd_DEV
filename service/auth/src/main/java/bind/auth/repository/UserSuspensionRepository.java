package bind.auth.repository;

import bind.auth.entity.UserSuspension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSuspensionRepository extends JpaRepository<UserSuspension, Long> {

    /**
     * 현재 유효한 정지 상태인 사용자 조회
     */
    Optional<UserSuspension> findByUserIdAndIsActiveTrue(String userId);

    /**
     * 유저의 정지 이력 전체 조회
     */
    Optional<UserSuspension> findTopByUserIdOrderBySuspendedAtDesc(String userId);
}
