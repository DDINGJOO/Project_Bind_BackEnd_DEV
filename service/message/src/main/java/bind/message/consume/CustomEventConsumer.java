package bind.message.consume;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper; // 생성자 주입으로 변경

    @KafkaListener(topics = "#{'${app.kafka.event-topics}'.split(',')}", groupId = "${app.kafka.consumer-group}")
    public void consume(Event<? extends EventPayload> event) {
        eventHandlerFactory.handleEvent(event);
    }
}
