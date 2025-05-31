package bind.userInfo.repository;

import bind.userInfo.entity.UserInterest;
import data.enums.instrument.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUserId(String userId);
    List<UserInterest> findByInterest(Instrument interest);
    List<UserInterest> findByUserIdIn(List<String> userIds);

    List<UserInterest> findByUserIdAndInterest(String userId, Instrument interest);

    void deleteAllByUserId(String userId);

}
