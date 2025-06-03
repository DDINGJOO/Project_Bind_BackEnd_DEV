package bind.message.repository;

import bind.message.entity.Message;
import bind.message.entity.UserProfileSnapshot;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserProfileSnapshotRepository extends JpaRepository<UserProfileSnapshot, String> {


    List<UserProfileSnapshot> findAllByUserIdIn(Collection<String> userIds);


    Optional<UserProfileSnapshot> findByUserId(String senderId);

}
