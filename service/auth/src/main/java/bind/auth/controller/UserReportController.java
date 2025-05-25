package bind.auth.controller;

import bind.auth.dto.request.ReportRequest;
import bind.auth.dto.response.UserReportResponse;
import bind.auth.service.UserReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import security.jwt.JwtProvider;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-report")
public class UserReportController {

    private final UserReportService userReportService;
    private final JwtProvider jwtProvider;


    /**
     * 사용자 신고 API
     * @param request 신고 요청 정보
     * @param bearerToken JWT 토큰 (Authorization 헤더에 포함)
     * @return 신고 성공 응답
     */
    @PostMapping
    public ResponseEntity<Void> reportUser(@RequestBody ReportRequest request,
                                           @RequestHeader("Authorization") String bearerToken) {
        String userId = jwtProvider.getUserIdFromToken(bearerToken);
        userReportService.reportUser(userId,request);
        return ResponseEntity.ok().build();
    }

    /**
     * 관리자용 사용자 신고 조회 API
     * @param bearerToken JWT 토큰 (Authorization 헤더에 포함)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 신고 목록 응답
     */
    @GetMapping("/my")
    public ResponseEntity<Page<UserReportResponse>> getMyReports(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String reporterId = jwtProvider.getUserIdFromToken(bearerToken);
        Pageable pageable = PageRequest.of(page, size, Sort.by("reportedAt").descending());
        return ResponseEntity.ok(userReportService.getReportsByReporter(reporterId, pageable));
    }
}
