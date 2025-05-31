package bind.userInfo.repository;

import bind.userInfo.entity.UserProfileHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProfileHistoryRepository extends JpaRepository<UserProfileHistory, Long> {
    List<UserProfileHistory> findByUserIdOrderByChangedAtDesc(String userId);
}
