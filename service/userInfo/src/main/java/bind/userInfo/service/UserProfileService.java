package bind.userInfo.service;


import bind.userInfo.dto.request.UserProfileCreateRequest;
import bind.userInfo.dto.request.UserProfileUpdateRequest;
import bind.userInfo.dto.response.UserProfileSummaryResponse;
import bind.userInfo.entity.UserGenre;
import bind.userInfo.entity.UserInterest;
import bind.userInfo.entity.UserProfile;
import bind.userInfo.exception.ProfileErrorCode;
import bind.userInfo.exception.ProfileException;
import bind.userInfo.repository.UserGenreRepository;
import bind.userInfo.repository.UserInterestRepository;
import bind.userInfo.repository.UserProfileRepository;
import data.enums.Genre;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserInterestRepository userInterestRepository;
    private final UserGenreRepository userGenreRepository;


    // --- 단건 조회 ---
    @Transactional(readOnly = true)
    public UserProfileSummaryResponse getProfile(String userId) {
        UserProfile profile = getProfileOrThrow(userId);
        List<UserInterest> interests = userInterestRepository.findByUserId(userId);
        List<UserGenre> genres = userGenreRepository.findByUserId(userId);
        return UserProfileConverter.toResponse(profile, interests, genres);
    }

    @Transactional(readOnly = true)
    public Page<UserProfileSummaryResponse> searchProfiles(
            String nickname, Location location,
            List<Instrument> interests, List<Genre> genres,
            Pageable pageable) {

        // 1. QueryDSL 기반 페이징 필터 검색
        Page<UserProfile> profiles = userProfileRepository
                .searchProfilesDsl(nickname, location, interests, genres, pageable);

        // 2. userId 목록 추출
        List<String> userIds = profiles.stream()
                .map(UserProfile::getUserId)
                .filter(Objects::nonNull)
                .toList();

        Map<String, List<UserInterest>> interestsMap = userIds.isEmpty() ? Collections.emptyMap()
                : userInterestRepository.findAllByUserIdIn(userIds)
                .stream().collect(Collectors.groupingBy(UserInterest::getUserId));

        Map<String, List<UserGenre>> genresMap = userIds.isEmpty() ? Collections.emptyMap()
                : userGenreRepository.findAllByUserIdIn(userIds)
                .stream().collect(Collectors.groupingBy(UserGenre::getUserId));

        // 3. 매핑
        return profiles.map(profile -> {
            List<UserInterest> interestList = interestsMap.getOrDefault(profile.getUserId(), Collections.emptyList());
            List<UserGenre> genreList = genresMap.getOrDefault(profile.getUserId(), Collections.emptyList());
            return UserProfileConverter.toResponse(profile, interestList, genreList);
        });
    }
    // --- 프로필 생성 ---
    @Transactional
    public UserProfileSummaryResponse create(UserProfileCreateRequest req) {

        if (userProfileRepository.existsByUserId((req.getUserId()))) {
            throw new ProfileException(ProfileErrorCode.PROFILE_ALREADY_EXISTS);
        }


        // 프로필 생성 시각
        LocalDateTime now = LocalDateTime.now();

        UserProfile profile = UserProfile.builder()
                .userId(req.getUserId())
                .nickname(req.getNickname())
                .profileImageUrl(req.getProfileImageUrl())
                .introduction(req.getIntroduction())
                .location(req.getLocation())
                .profilePublic(req.getProfilePublic())
                .createdAt(now)
                .updatedAt(now)
                .build();
        userProfileRepository.save(profile);

        // 연관 테이블 일괄 저장
        saveUserInterests(req.getInstruments(), profile.getUserId(), now);
        saveUserGenres(req.getGenres(), profile.getUserId(), now);

        return getProfile(profile.getUserId());
    }

    // --- 프로필 업데이트 ---
    @Transactional
    public UserProfileSummaryResponse updateProfile(String userId, UserProfileUpdateRequest req) {

        UserProfile profile = getProfileOrThrow(userId);
        patchProfile(profile, req);
        userProfileRepository.save(profile);

        // 흥미(관심사) 일괄 갱신
        if (req.getInterests() != null) {
            userInterestRepository.deleteAllByUserId(userId);
            saveUserInterests(req.getInterests(), userId, LocalDateTime.now());
        }
        // 장르 일괄 갱신
        if (req.getGenres() != null) {
            userGenreRepository.deleteAllByUserId(userId);
            saveUserGenres(req.getGenres(), userId, LocalDateTime.now());
        }
        return getProfile(userId);
    }

    // --- 프로필 삭제 ---
    @Transactional
    public void deleteProfile(String userId) {
        UserProfile profile = getProfileOrThrow(userId);
        userProfileRepository.delete(profile);
        userInterestRepository.deleteAllByUserId(userId);
        userGenreRepository.deleteAllByUserId(userId);
    }

    // ==============================
    // --- Private Helper Methods ---
    // ==============================

    private UserProfile getProfileOrThrow(String userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new ProfileException(ProfileErrorCode.PROFILE_NOT_FOUND));
    }

    private void patchProfile(UserProfile profile, UserProfileUpdateRequest req) {
        Optional.ofNullable(req.getNickname()).ifPresent(profile::setNickname);
        Optional.ofNullable(req.getProfileImageUrl()).ifPresent(profile::setProfileImageUrl);
        Optional.ofNullable(req.getIntroduction()).ifPresent(profile::setIntroduction);
        Optional.ofNullable(req.getLocation()).ifPresent(profile::setLocation);
        Optional.ofNullable(req.getProfilePublic()).ifPresent(profile::setProfilePublic);
        profile.setUpdatedAt(LocalDateTime.now());
    }

    private void saveUserInterests(List<Instrument> interests, String userId, LocalDateTime now) {
        if (interests == null) return;
        userInterestRepository.saveAll(
                interests.stream()
                        .map(inst -> UserInterest.builder()
                                .userId(userId)
                                .interest(inst)
                                .createdAt(now)
                                .build())
                        .collect(Collectors.toList())
        );
    }

    private void saveUserGenres(List<Genre> genres, String userId, LocalDateTime now) {
        if (genres == null) return;
        userGenreRepository.saveAll(
                genres.stream()
                        .map(genre -> UserGenre.builder()
                                .userId(userId)
                                .genre(genre)
                                .createdAt(now)
                                .build())
                        .collect(Collectors.toList())
        );
    }

    // --- DTO 변환 모듈 ---
    private static class UserProfileConverter {
        static UserProfileSummaryResponse toResponse(UserProfile p, List<UserInterest> interests, List<UserGenre> genres) {
            UserProfileSummaryResponse dto = new UserProfileSummaryResponse();
            dto.setUserId(p.getUserId());
            dto.setNickname(p.getNickname());
            dto.setProfileImageUrl(p.getProfileImageUrl());
            dto.setIntroduction(p.getIntroduction());
            dto.setLocation(p.getLocation());
            dto.setProfilePublic(p.getProfilePublic());
            dto.setInterests(interests == null ? List.of() :
                    interests.stream().map(UserInterest::getInterest).collect(Collectors.toList()));
            dto.setGenres(genres == null ? List.of() :
                    genres.stream().map(UserGenre::getGenre).collect(Collectors.toList()));
            return dto;
        }
    }
}
