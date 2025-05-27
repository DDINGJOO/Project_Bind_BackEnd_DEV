package event.producer;

import event.constant.EventType;
import event.events.Event;
import event.events.EventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T extends EventPayload> void send(EventType type, T payload, String topic) {
        Event<T> event = Event.<T>builder()
                .type(type)
                .payload(payload)
                .timestamp(System.currentTimeMillis())
                .build();

        kafkaTemplate.send(topic, event);
    }
}
