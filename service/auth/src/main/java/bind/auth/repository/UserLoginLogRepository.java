package bind.auth.repository;

import bind.auth.entity.UserLoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoginLogRepository extends JpaRepository<UserLoginLog, Long> {
    /**
     * 사용자 로그인 로그 조회(페이징)
     * 시간순으로 정렬해줘

     * @param pageable 페이징 정보
     * @return 사용자 로그인 로그 페이지
     */
    Page<UserLoginLog> findAll( Pageable pageable);
}
