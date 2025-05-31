package event.consumer;

import event.domain.Event;
import event.domain.EventPayload;
import event.handler.EventHandlerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class EventConsumer {

    private final EventHandlerFactory eventHandlerFactory;

    @KafkaListener(topics = "msa-event-topic", groupId = "msa-event-group")
    public void consume(ConsumerRecord<String, Event<? extends EventPayload>> record) {
        Event<? extends EventPayload> event = record.value();
        eventHandlerFactory.handleEvent(event);
    }
}
