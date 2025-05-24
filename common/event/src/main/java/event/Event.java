package event;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event<T extends EventPayload> implements Serializable {
    private String type;
    private T payload;
    private LocalDateTime occurredAt;
}
