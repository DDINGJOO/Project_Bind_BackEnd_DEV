package event.payload;

import event.events.EventPayload;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWithdrawEventPayload implements EventPayload, Serializable {
    private String userId;
    private String reason;
    private LocalDateTime withdrawAt;
}
