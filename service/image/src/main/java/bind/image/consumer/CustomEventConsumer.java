package bind.image.consumer;

import event.domain.Event;
import event.domain.EventPayload;
import event.handler.EventHandlerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomEventConsumer {

    private final EventHandlerFactory eventHandlerFactory;

    @KafkaListener(topics = "#{'${app.kafka.event-topics}'.split(',')}", groupId = "${app.kafka.consumer-group}")
    public void consume(ConsumerRecord<String, Event<? extends EventPayload>> record) {
        Event<? extends EventPayload> event = record.value();
        log.info("이벤트 수신: {}", event);
        eventHandlerFactory.handleEvent(event);
    }
}
