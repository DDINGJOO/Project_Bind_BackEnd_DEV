package bind.userInfo.dto.request;

import data.enums.instrument.Instrument;
import data.enums.location.Location;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class UserProfileCreateRequest {
    private String userId;
    private String nickname;
    private String profileImageUrl;
    private String introduction;
    private Location location;
    private String gender;
    private String birthdate; // yyyy-MM-dd (프론트에서 String으로 전달, 서버에서 LocalDate로 파싱)
    private Boolean profilePublic;
    private List<Instrument> instruments; // 관심사(태그/쉼표 구분), 별도 테이블 분리도 가능
}
