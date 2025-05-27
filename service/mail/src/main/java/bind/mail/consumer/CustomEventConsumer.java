package bind.mail.consumer;

import event.domain.BaseEvent;
import event.handler.EventHandlerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomEventConsumer {

    private final EventHandlerFactory eventHandlerFactory;

    // 여러 토픽을 한 번에 주입받습니다.
    @KafkaListener(topics = "#{'${app.kafka.event-topics}'.split(',')}", groupId = "${app.kafka.consumer-group}")
    public void consume(ConsumerRecord<String, Object> record) {
        BaseEvent event = (BaseEvent) record.value();
        eventHandlerFactory.handleEvent(event);
    }
}
