package bind.userInfo.repository.querydsl;


import bind.userInfo.entity.UserProfile;
import data.enums.Genre;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserProfileRepositoryCustom {
    Page<UserProfile> searchProfilesDsl(
            String nickname,
            Location location,
            List<Instrument> interests,
            List<Genre> genres,
            Pageable pageable
    );
}
