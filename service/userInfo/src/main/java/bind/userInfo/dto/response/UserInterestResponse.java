package bind.userInfo.dto.response;

import data.enums.instrument.Instrument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Schema(description = "유저의 관심사(악기) 응답 DTO")
public class UserInterestResponse {

    @Schema(description = "관심사(악기) 고유 식별자", example = "101")
    private Long id;

    @Schema(description = "유저의 고유 식별자", example = "user-1234")
    private String userId;

    @Schema(
            description = "관심 악기(ENUM: GUITAR, DRUM, VOCAL 등)",
            example = "DRUM",
            implementation = Instrument.class
    )
    private Instrument interest;

    @Schema(
            description = "등록일시(ISO 8601, yyyy-MM-dd'T'HH:mm:ss)",
            example = "2024-06-09T13:27:11"
    )
    private LocalDateTime createdAt;
}
