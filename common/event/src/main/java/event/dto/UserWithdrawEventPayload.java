package event.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import event.domain.EventPayload;
import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@JsonTypeName("UserWithdrawEventPayload")
public class UserWithdrawEventPayload implements EventPayload {

    private String userId;
    private String email;



}
