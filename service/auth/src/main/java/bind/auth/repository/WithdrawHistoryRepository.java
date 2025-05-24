
package bind.auth.repository;

import bind.auth.entity.WithdrawHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawHistoryRepository extends JpaRepository<WithdrawHistory, Long> {
}
