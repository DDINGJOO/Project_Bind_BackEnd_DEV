package event.domain;

import event.constant.EventType;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEvent {
    private EventType eventType;
}
