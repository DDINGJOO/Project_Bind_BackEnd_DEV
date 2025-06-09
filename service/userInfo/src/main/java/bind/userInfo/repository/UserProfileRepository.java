package bind.userInfo.repository;

import bind.userInfo.entity.UserProfile;
import data.enums.location.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserProfileRepository
        extends JpaRepository<UserProfile, String> {

    Optional<UserProfile> findByUserId(String userId);
    Optional<UserProfile> findByNickname(String nickname);


    /**
     * 페이징 + 단순 검색(연관관계 없이 UserProfile만)
     * 연관 데이터(관심사, 장르 등)는 서비스 레이어에서 userId로 배치 조회.
     */
    @Query("""
                SELECT up
                FROM UserProfile up
                WHERE (:nickname IS NULL OR up.nickname LIKE %:nickname%)
                  AND (:location IS NULL OR up.location = :location)
                ORDER BY up.nickname ASC
            """)
    Page<UserProfile> searchProfilesBasic(
            @Param("nickname") String nickname,
            @Param("location") Location location,
            Pageable pageable
    );
    boolean existsByUserId(String userId);

    boolean existsByNickname(String nickname);

}
