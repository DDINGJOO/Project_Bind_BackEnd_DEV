package bind.auth.repository;

import bind.auth.entity.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory , Long> {
    boolean existsByUserIdAndPasswordHash(String user_id, String passwordHash);
}
