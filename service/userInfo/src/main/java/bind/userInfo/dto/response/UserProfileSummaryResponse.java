package bind.userInfo.dto.response;

import data.enums.Genre;
import data.enums.instrument.Instrument;
import data.enums.location.Location;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileSummaryResponse {
    private String userId;
    private String nickname;
    private String profileImageUrl;
    private String introduction;
    private Location location;
    private Boolean profilePublic;
    private List<Instrument> interests; // 흥미 목록
    private List<Genre> genres; // 장르 목록 (예: "ROCK", "POP" 등, 별도 테이블 분리도 가능)
}
