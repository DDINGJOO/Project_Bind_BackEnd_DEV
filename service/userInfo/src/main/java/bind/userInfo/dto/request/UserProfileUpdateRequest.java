package bind.userInfo.dto.request;


import data.enums.instrument.Instrument;
import data.enums.location.Location;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileUpdateRequest {
    private String nickname;
    private String profileImageUrl;
    private String introduction;
    private Location location;
    private Boolean profilePublic;
    private List<Instrument> interests; // 새롭게 지정할 전체 흥미 목록(악기 N개)
}
