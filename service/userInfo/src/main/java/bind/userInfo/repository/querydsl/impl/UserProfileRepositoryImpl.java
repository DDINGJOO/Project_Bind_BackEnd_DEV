package bind.userInfo.repository.querydsl.impl;


import bind.userInfo.entity.QUserGenre;
import bind.userInfo.entity.QUserInterest;
import bind.userInfo.entity.QUserProfile;
import bind.userInfo.entity.UserProfile;
import bind.userInfo.repository.querydsl.UserProfileRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import data.enums.Genre;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

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
        QUserInterest interest = QUserInterest.userInterest;
        QUserGenre genre = QUserGenre.userGenre;

        // 쿼리 본체
        var query = queryFactory.selectFrom(profile)
                .leftJoin(profile.userInterests, interest).fetchJoin()
                .leftJoin(profile.userGenres, genre).fetchJoin()
                .where(
                        nickname == null ? null : profile.nickname.containsIgnoreCase(nickname),
                        location == null ? null : profile.location.eq(location),
                        CollectionUtils.isEmpty(interests) ? null : interest.interest.in(interests),
                        CollectionUtils.isEmpty(genres) ? null : genre.genre.in(genres)
                )
                .distinct();

        // 페이징 적용
        List<UserProfile> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        Long total = queryFactory
                .select(profile.countDistinct())
                .from(profile)
                .leftJoin(profile.userInterests, interest)
                .leftJoin(profile.userGenres, genre)
                .where(
                        nickname == null ? null : profile.nickname.containsIgnoreCase(nickname),
                        location == null ? null : profile.location.eq(location),
                        CollectionUtils.isEmpty(interests) ? null : interest.interest.in(interests),
                        CollectionUtils.isEmpty(genres) ? null : genre.genre.in(genres)
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total == null ? 0 : total);
    }
}
