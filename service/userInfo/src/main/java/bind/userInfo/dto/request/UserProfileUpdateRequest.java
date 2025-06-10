package bind.userInfo.dto.request;


import data.enums.Genre;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "유저 프로필 수정 요청 DTO")
public class UserProfileUpdateRequest {

    @Schema(description = "수정할 닉네임", example = "베이스장인")
    private String nickname;


    @Schema(description = "자기소개(최대 200자)", example = "드럼/기타/베이스 합주 가능합니다.")
    @Size(max = 200, message = "소개는 최대 200자까지 입력할 수 있습니다.")
    private String introduction;

    @Schema(description = "지역(ENUM: SEOUL, GYEONGGI, ...)", example = "SEOUL", implementation = Location.class)
    private Location location;

    @Schema(description = "프로필 공개 여부", example = "true")
    private Boolean profilePublic;

    private Long profileId;

    @Schema(
            description = "흥미 악기 전체 목록 (ENUM: GUITAR, DRUM, VOCAL 등)",
            example = "[\"GUITAR\", \"VOCAL\"]",
            implementation = Instrument.class
    )
    @Size(max = 5, message = "최대 5개까지 악기를 선택할 수 있습니다.")
    private List<Instrument> interests;

    @Schema(
            description = "선호 장르 전체 목록 (ENUM: ROCK, POP 등)",
            example = "[\"ROCK\", \"JAZZ\"]",
            implementation = Genre.class
    )
    @Size(max = 5, message = "최대 5개까지 장르를 선택할 수 있습니다.")
    private List<Genre> genres;
}
