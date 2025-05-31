package bind.userInfo.dto.response;

import data.enums.instrument.Instrument;
import data.enums.location.Location;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileSummaryResponse {
    private String userId;
    private String nickname;
    private String profileImageId;
    private String introduction;
    private Location location;
    private Boolean profilePublic;
    private List<Instrument> interests; // 흥미 목록
}
