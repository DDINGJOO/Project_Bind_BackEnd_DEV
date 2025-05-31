package bind.userInfo.repository;


import bind.userInfo.entity.UserInterest;
import bind.userInfo.entity.UserProfile;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UserProfileSpecs {
    public static Specification<UserProfile> withFilters(
            String nickname, Location location, List<Instrument> interests) {
        return (root, query, cb) -> {
            Predicate p = cb.conjunction();

            if (nickname != null && !nickname.isEmpty()) {
                p = cb.and(p, cb.like(root.get("nickname"), "%" + nickname + "%"));
            }
            if (location != null) {
                p = cb.and(p, cb.equal(root.get("location"), location));
            }
            if (interests != null && !interests.isEmpty()) {
                // UserInterest와 조인
                Join<UserProfile, UserInterest> interestJoin = root.join("userInterests", JoinType.INNER);
                p = cb.and(p, interestJoin.get("interest").in(interests));
                query.distinct(true);
            }
            return p;
        };
    }
}
