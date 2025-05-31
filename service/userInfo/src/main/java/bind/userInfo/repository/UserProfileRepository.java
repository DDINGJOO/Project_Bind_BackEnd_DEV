package bind.userInfo.repository;

import bind.userInfo.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    Optional<UserProfile> findByNickname(String nickname);
    List<UserProfile> findByProfilePublicTrue();
    List<UserProfile> findByLocation(String location); // Enum 사용 시 location.name()으로 조회

    // 복합 조건 등 추가 쿼리 필요하면 메소드 네이밍 or @Query로 추가
}
