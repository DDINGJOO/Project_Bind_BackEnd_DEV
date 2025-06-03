package bind.userInfo.controller;


import bind.userInfo.dto.request.UserProfileCreateRequest;
import bind.userInfo.dto.request.UserProfileUpdateRequest;
import bind.userInfo.dto.response.UserProfileSummaryResponse;
import bind.userInfo.exception.ProfileException;
import bind.userInfo.service.EventPubService;
import bind.userInfo.service.UserProfileService;
import data.BaseResponse;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final EventPubService eventPubService;

    // 1. 단건 조회 (흥미/관심 목록 포함)
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
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserProfileSummaryResponse> profiles = userProfileService.searchProfiles(
                nickname, location, interests, pageable
        );

        Page<BaseResponse<UserProfileSummaryResponse>> responsePage = profiles.map(BaseResponse::success);
        return ResponseEntity.ok(responsePage);
    }

    // 3. 생성
    @PostMapping
    public ResponseEntity<BaseResponse<UserProfileSummaryResponse>> createProfile(
            @RequestBody UserProfileCreateRequest request
    ) {
        UserProfileSummaryResponse response;
        try{
            response = userProfileService.create(request);
        }catch (ProfileException e){
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("프로필 생성 중 오류가 발생했습니다."));
        }

        try{
            eventPubService.userProfileCreatedEventPub(
                    response
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("카프카 메세지 발행 중 오류가 발생했습니다."));
        }
        return ResponseEntity.ok(BaseResponse.success(response));
    }



    // 4. 업데이트 (부분/전체)
    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse<UserProfileSummaryResponse>> updateProfile(
            @PathVariable String userId,
            @RequestBody UserProfileUpdateRequest request
    ) {
        var response  = userProfileService.updateProfile(userId, request);

        try {
            eventPubService.userProfileUpdatedEventPub(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("카프카 메세지 발행 중 오류가 발생했습니다."));
        }
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    // 5. 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable String userId) {
        userProfileService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }
}
