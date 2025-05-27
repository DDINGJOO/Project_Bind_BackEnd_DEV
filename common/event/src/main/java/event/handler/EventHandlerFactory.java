package event.handler;

import event.constant.EventType;
import event.domain.BaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventHandlerFactory {

    private final Map<EventType, EventHandler> handlers;

    @Autowired
    public EventHandlerFactory(List<EventHandler> handlerList) {
        handlers = handlerList.stream()
                .collect(Collectors.toMap(EventHandler::supportedType, Function.identity()));
    }

    public void handleEvent(BaseEvent event) {
        EventHandler handler = handlers.get(event.getEventType());
        if (handler != null) {
            handler.handle(event);
        } else {
            throw new UnsupportedOperationException("Unhandled event type: " + event.getEventType());
        }
    }
}
