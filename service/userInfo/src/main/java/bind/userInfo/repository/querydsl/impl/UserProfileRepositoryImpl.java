package bind.userInfo.repository.querydsl.impl;


import bind.userInfo.entity.QUserGenre;
import bind.userInfo.entity.QUserInterest;
import bind.userInfo.entity.QUserProfile;
import bind.userInfo.entity.UserProfile;
import bind.userInfo.repository.querydsl.UserProfileRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import data.enums.Genre;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class UserProfileRepositoryImpl implements UserProfileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserProfileRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public Page<UserProfile> searchProfilesDsl(
            String nickname,
            Location location,
            List<Instrument> interests,
            List<Genre> genres,
            Pageable pageable
    ) {
        QUserProfile profile = QUserProfile.userProfile;
        QUserInterest userInterest = QUserInterest.userInterest;
        QUserGenre userGenre = QUserGenre.userGenre;

        // where절 빌드
        var whereBuilder = new BooleanBuilder();

        if (nickname != null && !nickname.isBlank()) {
            whereBuilder.and(profile.nickname.containsIgnoreCase(nickname));
        }
        if (location != null) {
            whereBuilder.and(profile.location.eq(location));
        }
        if (interests != null && !interests.isEmpty()) {
            // 관심사에 해당하는 userId만
            whereBuilder.and(profile.userId.in(
                    JPAExpressions
                            .select(userInterest.userId)
                            .from(userInterest)
                            .where(userInterest.interest.in(interests))
            ));
        }
        if (genres != null && !genres.isEmpty()) {
            // 장르에 해당하는 userId만
            whereBuilder.and(profile.userId.in(
                    JPAExpressions
                            .select(userGenre.userId)
                            .from(userGenre)
                            .where(userGenre.genre.in(genres))
            ));
        }

        // 1차 쿼리: UserProfile 페이징 (관계 Fetch X)
        List<UserProfile> results = queryFactory
                .selectFrom(profile)
                .where(whereBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        Long total = queryFactory
                .select(profile.count())
                .from(profile)
                .where(whereBuilder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total == null ? 0 : total);
    }

}
