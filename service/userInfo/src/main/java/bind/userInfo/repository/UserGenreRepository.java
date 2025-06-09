package bind.userInfo.repository;


import bind.userInfo.entity.UserGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGenreRepository extends JpaRepository<UserGenre , Long> {
    List<UserGenre> findByUserId(String userId);

    List<UserGenre> findAllByUserIdIn(List<String> userIds);
    void deleteAllByUserId(String userId);


}


/*
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUserId(String userId);

    void deleteAllByUserId(String userId);



}
 */
