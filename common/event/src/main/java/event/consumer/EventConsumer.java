package event.consumer;

import event.domain.BaseEvent;
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
    public void consume(ConsumerRecord<String, Object> record) {
        BaseEvent event = (BaseEvent) record.value();
        eventHandlerFactory.handleEvent(event);
    }
}
