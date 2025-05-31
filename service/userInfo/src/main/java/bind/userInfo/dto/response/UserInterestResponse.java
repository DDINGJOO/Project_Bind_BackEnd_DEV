package bind.userInfo.dto.response;

import data.enums.instrument.Instrument;
import lombok.Data;

@Data
public class UserInterestResponse {
    private Long id;
    private String userId;
    private Instrument interest;
    private String createdAt;
}
