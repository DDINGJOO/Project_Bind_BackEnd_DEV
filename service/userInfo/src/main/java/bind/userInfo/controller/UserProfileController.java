package bind.userInfo.controller;


import bind.userInfo.dto.request.UserProfileCreateRequest;
import bind.userInfo.dto.request.UserProfileUpdateRequest;
import bind.userInfo.dto.response.UserProfileSummaryResponse;
import bind.userInfo.service.UserProfileService;
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

    // 1. 단건 조회 (흥미/관심 목록 포함)
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileSummaryResponse> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    // 2. 페이징 검색 (닉네임/지역/관심 N개 필터링, 흥미 목록 포함)
    @GetMapping
    public ResponseEntity<Page<UserProfileSummaryResponse>> searchProfiles(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Location location,
            @RequestParam(required = false) List<Instrument> interests, // /?interests=DRUM&interests=VOCAL
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(
                userProfileService.searchProfiles(nickname, location, interests, pageable)
        );
    }

    // 3. 생성
    @PostMapping
    public ResponseEntity<UserProfileSummaryResponse> createProfile(
            @RequestBody UserProfileCreateRequest request
    ) {
        return ResponseEntity.ok(userProfileService.create(request));
    }

    // 4. 업데이트 (부분/전체)
    @PatchMapping("/{userId}")
    public ResponseEntity<UserProfileSummaryResponse> updateProfile(
            @PathVariable String userId,
            @RequestBody UserProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }

    // 5. 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable String userId) {
        userProfileService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }
}
