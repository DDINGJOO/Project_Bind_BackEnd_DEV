package event.handler;

import event.constant.EventType;
import event.domain.Event;
import event.domain.EventPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventHandlerFactory {

    private final Map<EventType, EventHandler<? extends EventPayload>> handlers;

    @Autowired
    public EventHandlerFactory(List<EventHandler<? extends EventPayload>> handlerList) {
        handlers = handlerList.stream()
                .collect(Collectors.toMap(EventHandler::supportedType, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T extends EventPayload> void handleEvent(Event<T> event) {
        EventHandler<T> handler = (EventHandler<T>) handlers.get(event.getType());
        if (handler != null) {
            handler.handle(event);
        } else {
            throw new UnsupportedOperationException("Unhandled event type: " + event.getType());
        }
    }
}
