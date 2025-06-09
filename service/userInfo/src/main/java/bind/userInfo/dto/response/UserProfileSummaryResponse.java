package bind.userInfo.dto.response;

import data.enums.Genre;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
@Schema(description = "유저 프로필 요약 응답 DTO")
public class UserProfileSummaryResponse {

    @Schema(description = "유저 고유 식별자", example = "user-1234")
    private String userId;

    @Schema(description = "닉네임", example = "드럼의신")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/profiles/user-1234.jpg")
    private String profileImageUrl;

    @Schema(description = "자기소개", example = "서울에서 활동하는 베이시스트")
    private String introduction;

    @Schema(
            description = "활동 지역(ENUM: SEOUL, GYEONGGI 등)",
            example = "SEOUL",
            implementation = Location.class
    )
    private Location location;

    @Schema(description = "프로필 공개 여부", example = "true")
    private Boolean profilePublic;

    @Schema(
            description = "관심 악기 전체 목록 (ENUM: GUITAR, DRUM, VOCAL 등)",
            example = "[\"GUITAR\", \"VOCAL\"]",
            implementation = Instrument.class
    )
    private List<Instrument> interests;

    @Schema(
            description = "선호 장르 전체 목록 (ENUM: ROCK, POP 등)",
            example = "[\"ROCK\", \"POP\"]",
            implementation = Genre.class
    )
    private List<Genre> genres;
}
