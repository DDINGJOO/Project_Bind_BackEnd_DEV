package bind.auth.dto.response;

import data.enums.auth.ReportType;

import java.time.LocalDateTime;

// 신고 응답 DTO
public record UserReportResponse(
        Long id,
        String reportedUserId,
        String reporterUserId,
        ReportType reportType,
        String details,
        LocalDateTime reportedAt,
        boolean isResolved,
        LocalDateTime resolvedAt
) {}

