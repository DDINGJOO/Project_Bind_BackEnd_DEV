package bind.auth.dto.request;

import data.enums.auth.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public record ReportRequest(
        @NotBlank(message = "Reported user ID cannot be blank")
        String reportedUserId,
        @NotNull(message = "Report type cannot be null")
        ReportType reportType,

        String details

) {

}
