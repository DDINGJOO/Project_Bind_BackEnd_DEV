package payload;

import event.EventPayload;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginEventPayload implements EventPayload, Serializable {
    private String userId;
    private String ipAddress;
    private String userAgent;
    private boolean success;
    private LocalDateTime loginAt;
}
