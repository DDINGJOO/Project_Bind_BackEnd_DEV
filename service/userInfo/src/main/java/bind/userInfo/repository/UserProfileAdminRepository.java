package bind.userInfo.repository;

import bind.userInfo.entity.UserProfileAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileAdminRepository extends JpaRepository<UserProfileAdmin, String> {
    // userId로만 조회
}
