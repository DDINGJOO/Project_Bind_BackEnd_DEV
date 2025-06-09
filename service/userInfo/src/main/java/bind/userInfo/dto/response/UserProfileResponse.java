package bind.userInfo.dto.response;

import data.enums.location.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "유저 프로필 응답 DTO")
public class UserProfileResponse {

    @Schema(description = "유저 고유 식별자", example = "user-1234")
    private String userId;

    @Schema(description = "닉네임", example = "밴드짱")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/profiles/user-1234.jpg")
    private String profileImageUrl;

    @Schema(description = "자기소개", example = "5년차 기타리스트, 서울에서 활동")
    private String introduction;

    @Schema(
            description = "지역(ENUM: SEOUL, GYEONGGI 등)",
            example = "SEOUL",
            implementation = Location.class
    )
    private Location location;

    @Schema(description = "성별(M, F, 기타)", example = "M")
    private String gender;


    @Schema(description = "프로필 공개 여부", example = "true")
    private Boolean profilePublic;

    @Schema(description = "프로필 생성일시(ISO 8601, yyyy-MM-dd'T'HH:mm:ss)", example = "2024-06-09T13:27:11")
    private String createdAt;

    @Schema(description = "프로필 최종 수정일시(ISO 8601, yyyy-MM-dd'T'HH:mm:ss)", example = "2024-06-09T13:44:00")
    private String updatedAt;
}
