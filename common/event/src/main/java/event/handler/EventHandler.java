package event.handler;

import event.constant.EventType;
import event.domain.BaseEvent;

public interface EventHandler<T extends BaseEvent> {
    EventType supportedType();
    void handle(T event);
}
