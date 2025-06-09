package bind.userInfo.controller;


import bind.userInfo.dto.request.UserProfileCreateRequest;
import bind.userInfo.dto.request.UserProfileUpdateRequest;
import bind.userInfo.dto.response.UserProfileSummaryResponse;
import bind.userInfo.exception.ProfileException;
import bind.userInfo.service.EventPubService;
import bind.userInfo.service.UserProfileService;
import data.BaseResponse;
import data.enums.Genre;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import util.nicknamefilter.NicknameFilterService;
import util.nicknamefilter.exception.NickNameFilterException;

import java.util.List;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final EventPubService eventPubService;
    private final NicknameFilterService nicknameFilterService;




    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<UserProfileSummaryResponse>> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(
                BaseResponse.success(userProfileService.getProfile(userId))
        );
    }

    // 2. 페이징 검색 (닉네임/지역/관심 N개 필터링, 흥미 목록 포함)
    @GetMapping
    public ResponseEntity<Page<BaseResponse<UserProfileSummaryResponse>>> searchProfiles(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Location location,
            @RequestParam(required = false) List<Instrument> interests, // /?interests=DRUM&interests=VOCAL
            @RequestParam(required = false) List<Genre> genres, // /?genres=ROCK&genres=POP
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserProfileSummaryResponse> profiles = userProfileService.searchProfiles(
                nickname, location, interests, genres,pageable
        );

        Page<BaseResponse<UserProfileSummaryResponse>> responsePage = profiles.map(BaseResponse::success);
        return ResponseEntity.ok(responsePage);
    }

    // 3. 생성
    @PostMapping
    public ResponseEntity<BaseResponse<UserProfileSummaryResponse>> createProfile(
            @RequestBody UserProfileCreateRequest request
    ) {
        try{
            // 닉네임 필터링
            nicknameFilterService.validateNickname(request.getNickname());

            UserProfileSummaryResponse createdProfile = userProfileService.create(request);
            eventPubService.userProfileCreatedEventPub(createdProfile);


            return ResponseEntity.ok(BaseResponse.success(createdProfile));
        } catch (NickNameFilterException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (ProfileException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("프로필 생성 중 오류가 발생했습니다."));
        }
    }



    // 4. 업데이트 (부분/전체)
    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse<UserProfileSummaryResponse>> updateProfile(
            @PathVariable String userId,
            @RequestBody UserProfileUpdateRequest request
    ) {
        try {
            // 닉네임 필터링
            if (request.getNickname() != null) {
                nicknameFilterService.validateNickname(request.getNickname());
            }
            UserProfileSummaryResponse updatedProfile = userProfileService.updateProfile(userId, request);
            eventPubService.userProfileUpdatedEventPub(updatedProfile);
            return ResponseEntity.ok(BaseResponse.success(updatedProfile));
        } catch (NickNameFilterException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (ProfileException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("프로필 업데이트 중 오류가 발생했습니다."));
        }
    }

    // 5. 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable String userId) {
        userProfileService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }

}
