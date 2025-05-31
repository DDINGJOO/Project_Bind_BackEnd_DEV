package bind.userInfo.service;


import bind.userInfo.dto.request.UserProfileCreateRequest;
import bind.userInfo.dto.request.UserProfileUpdateRequest;
import bind.userInfo.dto.response.UserProfileSummaryResponse;
import bind.userInfo.entity.UserInterest;
import bind.userInfo.entity.UserProfile;
import bind.userInfo.exception.ProfileErrorCode;
import bind.userInfo.exception.ProfileException;
import bind.userInfo.repository.UserInterestRepository;
import bind.userInfo.repository.UserProfileRepository;
import bind.userInfo.repository.UserProfileSpecs;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserInterestRepository userInterestRepository;

    // 유저 단건 조회 (흥미목록까지 포함)
    public UserProfileSummaryResponse getProfile(String userId) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ProfileException(ProfileErrorCode.PROFILE_NOT_FOUND));
        List<UserInterest> interests = userInterestRepository.findByUserId(userId);
        return toResponse(profile, interests);
    }

    // 전체 유저 프로필 페이징 + 닉네임, 지역, 관심사(악기) 필터 조합
    public Page<UserProfileSummaryResponse> searchProfiles(
            String nickname, Location location, List<Instrument> interests, Pageable pageable) {
        Specification<UserProfile> spec = UserProfileSpecs.withFilters(nickname, location, interests);
        Page<UserProfile> profiles = userProfileRepository.findAll(spec, pageable);

        // 흥미목록 같이 넘기기
        List<String> userIds = profiles.getContent().stream()
                .map(UserProfile::getUserId).collect(Collectors.toList());
        List<UserInterest> allInterests = userInterestRepository.findByUserIdIn(userIds);

        // userId → 관심사 맵
        var interestMap = allInterests.stream().collect(Collectors.groupingBy(UserInterest::getUserId));

        Page<UserProfileSummaryResponse> result = profiles.map(p -> {
            List<UserInterest> iList = interestMap.getOrDefault(p.getUserId(), List.of());
            return toResponse(p, iList);
        });
        return result;
    }

    // CRUD 예시(생성)
    public UserProfileSummaryResponse create(UserProfileCreateRequest req) {
        UserProfile profile = UserProfile.builder()
                .userId(req.getUserId())
                .nickname(req.getNickname())
                .profileImageId(req.getProfileImageId())
                .introduction(req.getIntroduction())
                .location(req.getLocation())
                .profilePublic(req.getProfilePublic())
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        userProfileRepository.save(profile);

        // 흥미(관심사)도 별도 저장
        if (req.getInstruments() != null) {
            for (Instrument inst : req.getInstruments()) {
                userInterestRepository.save(UserInterest.builder()
                        .userId(profile.getUserId())
                        .interest(inst)
                        .createdAt(java.time.LocalDateTime.now())
                        .build());
            }
        }

        List<UserInterest> interests = userInterestRepository.findByUserId(profile.getUserId());
        return toResponse(profile, interests);
    }

    // 프로필 + 흥미목록 응답 변환
    private UserProfileSummaryResponse toResponse(UserProfile p, List<UserInterest> interests) {
        UserProfileSummaryResponse dto = new UserProfileSummaryResponse();
        dto.setUserId(p.getUserId());
        dto.setNickname(p.getNickname());
        dto.setProfileImageId(p.getProfileImageId());
        dto.setIntroduction(p.getIntroduction());
        dto.setLocation(p.getLocation());
        dto.setProfilePublic(p.getProfilePublic());
        dto.setInterests(interests.stream().map(UserInterest::getInterest).collect(Collectors.toList()));
        return dto;
    }

    public void deleteProfile(String userId) {
        // 1. 프로필 엔티티 가져오기
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ProfileException(ProfileErrorCode.PROFILE_NOT_FOUND));

        // 2. 프로필 삭제
        userProfileRepository.delete(profile);

        // 3. 해당 유저의 관심사(흥미) 모두 삭제
        userInterestRepository.deleteAllByUserId(userId);
    }

    @Transactional
    public UserProfileSummaryResponse updateProfile(String userId, UserProfileUpdateRequest req) {
        // 1. 프로필 엔티티 가져오기
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ProfileException(ProfileErrorCode.PROFILE_NOT_FOUND));

        // 2. 변경 필드 반영
        if (req.getNickname() != null) profile.setNickname(req.getNickname());
        if (req.getProfileImageId() != null) profile.setProfileImageId(req.getProfileImageId());
        if (req.getIntroduction() != null) profile.setIntroduction(req.getIntroduction());
        if (req.getLocation() != null) profile.setLocation(req.getLocation());
        if (req.getProfilePublic() != null) profile.setProfilePublic(req.getProfilePublic());
        profile.setUpdatedAt(java.time.LocalDateTime.now());

        userProfileRepository.save(profile);

        // 3. 관심사(흥미) 갱신: 모두 삭제 후 새로 저장
        if (req.getInterests() != null) {
            userInterestRepository.deleteAll(userInterestRepository.findByUserId(userId));
            for (Instrument inst : req.getInterests()) {
                userInterestRepository.save(UserInterest.builder()
                        .userId(userId)
                        .interest(inst)
                        .createdAt(java.time.LocalDateTime.now())
                        .build());
            }
        }

        // 4. 응답 변환
        List<UserInterest> interests = userInterestRepository.findByUserId(userId);
        return toResponse(profile, interests);
    }
}
