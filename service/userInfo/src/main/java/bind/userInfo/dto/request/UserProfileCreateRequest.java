package bind.userInfo.dto.request;

import data.enums.Genre;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;


@Data
@Schema(description = "유저 프로필 생성 요청 DTO")
public class UserProfileCreateRequest {

    @Schema(description = "유저 고유 식별자(자동 생성이 아니고, 클라이언트가 지정하는 경우에만 사용)", example = "user-1234", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userId;

    @Schema(description = "닉네임 (중복/필터링 검증 필요)", example = "밴드마스터", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "자기소개", example = "기타리스트, 10년차 밴드 경력자입니다.")
    private String introduction;

    @Schema(description = "지역(ENUM: SEOUL, GYEONGGI, ...)", example = "SEOUL", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Location.class)
    @NotNull
    private Location location;

    @Schema(description = "성별(M/F/기타)", example = "M")
    private String gender;


    @Schema(description = "프로필 공개 여부", example = "true")
    private Boolean profilePublic;

    @Schema(
            description = "관심 악기 목록 (ENUM: GUITAR, DRUM, VOCAL 등)",
            example = "[\"DRUM\", \"VOCAL\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Size(min = 1, message = "최소 1개 이상의 악기를 선택해야 합니다.")
    private List<Instrument> instruments;

    @Schema(
            description = "선호 장르 목록 (ENUM: ROCK, POP 등)",
            example = "[\"ROCK\", \"POP\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Size(min = 1, message = "최소 1개 이상의 장르를 선택해야 합니다.")
    private List<Genre> genres;
}
