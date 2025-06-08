package event.handler;

import event.constant.EventType;
import event.domain.Event;
import event.domain.EventPayload;

public interface EventHandler<T extends EventPayload> {
    EventType supportedType();
    void handle(Event<T> event);
}
