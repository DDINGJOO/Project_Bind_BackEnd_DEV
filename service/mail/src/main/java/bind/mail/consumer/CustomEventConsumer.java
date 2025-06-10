package bind.mail.consumer;

import event.domain.Event;
import event.domain.EventPayload;
import event.handler.EventHandlerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomEventConsumer {

    private final EventHandlerFactory eventHandlerFactory;

    @KafkaListener(topics = "#{'${app.kafka.event-topics}'.split(',')}", groupId = "${app.kafka.consumer-group}")
    public void consume(Event<? extends EventPayload> event) {
        eventHandlerFactory.handleEvent(event);
    }
}
