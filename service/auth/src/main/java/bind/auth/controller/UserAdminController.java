package bind.auth.controller;

import bind.auth.dto.request.UserSuspensionRequest;
import bind.auth.dto.response.UserReportResponse;
import bind.auth.dto.response.UserSuspensionStatusResponse;
import bind.auth.exception.AuthException;
import bind.auth.service.UserReportService;
import bind.auth.service.UserSuspensionService;
import data.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
@Tag(name = "User Suspension", description = "유저 정지 관리 API (관리자 전용)")
public class UserAdminController {

    private final UserSuspensionService suspensionService;
    private final UserReportService userReportService;

    /**
     * 유저를 일정 기간 동안 정지하거나 영구 정지합니다.
     *
     * @param request UserSuspendRequest (userId, 사유, 정지 시작/해제 일시)
     * @return 성공 시 200 OK, 실패 시 400 or 500 에러 응답
     */
    @Operation(
            summary = "유저 정지",
            description = "특정 유저를 기간 정지 또는 영구 정지합니다. 이미 정지된 유저는 중복 정지되지 않습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정지 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 이미 정지된 유저", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping("/suspend")
    public ResponseEntity<BaseResponse<Void>> suspendUser(
            @Valid @RequestBody UserSuspensionRequest request
    ) {
        try {
            suspensionService.suspend(request);
            return ResponseEntity.ok(BaseResponse.success());
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("정지 처리 중 오류가 발생했습니다."));
        }
    }

    /**
     * 정지 ID를 통해 해당 유저의 정지를 수동으로 해제합니다.
     *
     * @param id 정지 엔티티의 고유 ID
     * @return 성공 시 200 OK, 실패 시 400 or 500 에러 응답
     */
    @Operation(
            summary = "정지 해제",
            description = "정지 ID를 기반으로 해당 유저의 정지를 해제합니다. (is_active → false)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "해제 성공"),
                    @ApiResponse(responseCode = "400", description = "해당 정지 정보 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping("/suspension/lift/{id}")
    public ResponseEntity<BaseResponse<Void>> liftSuspension(
            @Parameter(description = "정지 레코드 ID", required = true)
            @PathVariable Long id
    ) {
        try {
            suspensionService.liftSuspension(id);
            return ResponseEntity.ok(BaseResponse.success());
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("정지 해제 중 오류가 발생했습니다."));
        }
    }


    /**
     * 정지 중인 유저 목록을 조회합니다.
     * 현재 정지 상태인 유저들의 목록을 반환하며, releaseAt이 미래이거나 null인 경우만 포함됩니다.
     * @return 정지 중인 유저들의 목록
     */
    @Operation(
            summary = "정지 중인 유저 목록 조회",
            description = "현재 정지 상태인 유저들의 목록을 조회합니다. releaseAt이 미래거나 null인 경우만 포함됩니다."
    )
    @GetMapping("/suspensions")
    public ResponseEntity<BaseResponse<List<UserSuspensionStatusResponse>>> getActiveSuspendedUsers() {
        try {
            List<UserSuspensionStatusResponse> result = suspensionService.getActiveSuspendedUsers();
            return ResponseEntity.ok(BaseResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("정지 목록 조회 중 오류 발생"));
        }
    }


    /**
     * 특정 유저의 정지 이력을 조회합니다.
     * @param userId 정지 이력을 조회할 유저의 ID
     * @return 특정 유저의 정지 이력 목록
     */
    @Operation(
            summary = "특정 유저의 정지 이력 조회",
            description = "지정된 유저 ID에 대해 모든 정지 이력을 조회합니다."
    )
    @GetMapping("/suspensions/{userId}")
    public ResponseEntity<BaseResponse<List<UserSuspensionStatusResponse>>> getSuspensionsByUser(
            @PathVariable String userId
    ) {
        try {
            List<UserSuspensionStatusResponse> result = suspensionService.getSuspensionHistoryByUser(userId);
            return ResponseEntity.ok(BaseResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.error("유저 정지 이력 조회 중 오류 발생"));
        }
    }


    /**
     * 유저 신고 목록을 페이지 단위로 조회합니다.
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 신고 목록과 함께 200 OK 응답
     */
    @Operation(
            summary = "유저 신고 목록 조회",
            description = "신고된 유저들의 목록을 페이지 단위로 조회합니다. 최신 신고부터 정렬됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "신고 목록 조회 성공"),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/reports")
    public ResponseEntity<Page<UserReportResponse>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("reportedAt").descending());
        return ResponseEntity.ok(userReportService.getReports(pageable));
    }

}
