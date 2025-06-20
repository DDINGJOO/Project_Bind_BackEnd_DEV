package bind.userInfo.repository;

import bind.userInfo.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUserId(String userId);

    List<UserInterest> findAllByUserIdIn(List<String> userIds);

    void deleteAllByUserId(String userId);



}
