package bind.userInfo.dto.request;

import data.enums.instrument.Instrument;
import lombok.Data;

@Data
public class UserInterestRequest {
    private Instrument interest;
}
