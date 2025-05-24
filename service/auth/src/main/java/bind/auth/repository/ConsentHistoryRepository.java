package bind.auth.repository;

import bind.auth.entity.ConsentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentHistoryRepository extends JpaRepository<ConsentHistory, Long> {
}
