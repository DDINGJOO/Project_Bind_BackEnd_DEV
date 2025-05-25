package bind.auth.repository;

import bind.auth.entity.UserReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    Page<UserReport> findAllByOrderByReportedAtDesc(Pageable pageable);
    Page<UserReport> findAllByReporterUserId_Id(String reporterId, Pageable pageable);
}
