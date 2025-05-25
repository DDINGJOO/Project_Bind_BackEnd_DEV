package bind.auth.service;


import bind.auth.dto.request.ReportRequest;
import bind.auth.dto.response.UserReportResponse;
import bind.auth.entity.User;
import bind.auth.entity.UserReport;
import bind.auth.exception.AuthException;
import bind.auth.repository.UserReportRepository;
import bind.auth.repository.UserRepository;
import exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static bind.auth.exception.AuthErrorCode.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserReportRepository userReportRepository;
    private final UserRepository userRepository;

    public void reportUser(String reporterId, ReportRequest request) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new AuthException(USER_NOT_FOUND.getMessage(), USER_NOT_FOUND));
        User reported = userRepository.findById(request.reportedUserId())
                .orElseThrow(() -> new AuthException(USER_NOT_FOUND.getMessage(), USER_NOT_FOUND));

        UserReport report = UserReport.builder()
                .reporterUserId(reporter)
                .reportedUserId(reported)
                .reportType(request.reportType())
                .details(request.details())
                .reportedAt(LocalDateTime.now())
                .isResolved(false)
                .build();

        userReportRepository.save(report);
    }
    public Page<UserReportResponse> getReports(Pageable pageable) {
        return userReportRepository.findAllByOrderByReportedAtDesc(pageable)
                .map(this::toResponse);
    }

    public Page<UserReportResponse> getReportsByReporter(String reporterId, Pageable pageable) {
        return userReportRepository.findAllByReporterUserId_Id(reporterId, pageable)
                .map(this::toResponse);
    }

    private UserReportResponse toResponse(UserReport report) {
        return new UserReportResponse(
                report.getId(),
                report.getReportedUserId().getId(),
                report.getReporterUserId().getId(),
                report.getReportType(),
                report.getDetails(),
                report.getReportedAt(),
                report.isResolved(),
                report.getResolvedAt()
        );
    }
}
