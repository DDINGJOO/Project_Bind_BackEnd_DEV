package bind.userInfo.repository;

import bind.userInfo.entity.UserInterest;
import data.enums.instrument.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUserId(String userId);

    void deleteAllByUserId(String userId);



}
