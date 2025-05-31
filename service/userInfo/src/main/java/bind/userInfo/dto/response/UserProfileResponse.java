package bind.userInfo.dto.response;

import data.enums.location.Location;
import lombok.Data;

@Data
public class UserProfileResponse {
    private String userId;
    private String nickname;
    private String profileImageId;
    private String introduction;
    private Location location;
    private String gender;
    private String birthdate;
    private Boolean profilePublic;
    private String createdAt;
    private String updatedAt;
}
