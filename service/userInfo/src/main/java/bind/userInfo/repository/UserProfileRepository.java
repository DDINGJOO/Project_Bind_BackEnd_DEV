package bind.userInfo.repository;

import bind.userInfo.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserProfileRepository
        extends JpaRepository<UserProfile, String>, JpaSpecificationExecutor<UserProfile> {

    Optional<UserProfile> findByUserId(String userId);
    Optional<UserProfile> findByNickname(String nickname);

    boolean existsByNickname(String nickname);

}
