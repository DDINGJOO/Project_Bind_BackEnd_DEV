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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import util.nicknamefilter.NicknameFilterService;
import util.nicknamefilter.exception.NickNameFilterException;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
@Tag(name = "UserProfile", description = "유저 프로필 관련 API")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final EventPubService eventPubService;
    private final NicknameFilterService nicknameFilterService;


    @Operation(summary = "유저 프로필 단건 조회", description = "userId로 유저 프로필을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = UserProfileSummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<UserProfileSummaryResponse>> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(
                BaseResponse.success(userProfileService.getProfile(userId))
        );
    }


    // 2. 페이징 검색 (닉네임/지역/관심 N개 필터링, 흥미 목록 포함)
    @Operation(
            summary = "프로필 검색(페이징)",
            description = "닉네임, 지역, 관심악기/장르 등으로 필터링해서 페이징 검색"
    )
    @Parameters({
            @Parameter(name = "nickname", description = "닉네임(부분 검색)", example = "홍길동"),
            @Parameter(name = "location", description = "활동 지역", schema = @Schema(implementation = Location.class)),
            @Parameter(name = "interests", description = "관심 악기 리스트", schema = @Schema(type = "array", implementation = Instrument.class)),
            @Parameter(name = "genres", description = "관심 장르 리스트", schema = @Schema(type = "array", implementation = Genre.class)),
    })
    @GetMapping
    public ResponseEntity<Page<BaseResponse<UserProfileSummaryResponse>>> searchProfiles(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Location location,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserProfileSummaryResponse> profiles = userProfileService.searchProfiles(
                nickname, location, pageable
        );

        Page<BaseResponse<UserProfileSummaryResponse>> responsePage = profiles.map(BaseResponse::success);
        return ResponseEntity.ok(responsePage);
    }

    // 3. 생성
    @Operation(summary = "프로필 생성", description = "새로운 유저 프로필 생성")
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
    @Operation(summary = "프로필 수정", description = "userId로 특정 프로필을 부분/전체 업데이트")
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
    @Operation(summary = "프로필 삭제", description = "userId로 특정 프로필을 삭제")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable String userId) {
        userProfileService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }

}
