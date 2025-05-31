package bind.userInfo.repository;

import bind.userInfo.entity.UserProfile;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository
        extends JpaRepository<UserProfile, String> {

    Optional<UserProfile> findByUserId(String userId);
    Optional<UserProfile> findByNickname(String nickname);

    @Query("""
        SELECT DISTINCT up
        FROM UserProfile up
        LEFT JOIN FETCH up.userInterests ui
        WHERE (:nickname IS NULL OR up.nickname LIKE %:nickname%)
          AND (:location IS NULL OR up.location = :location)
          AND (:interestsEmpty = TRUE OR ui.interest IN :interests)
    """)
    Page<UserProfile> searchProfiles(
            @Param("nickname") String nickname,
            @Param("location") Location location,
            @Param("interests") List<Instrument> interests,
            @Param("interestsEmpty") boolean interestsEmpty,
            Pageable pageable
    );
}
