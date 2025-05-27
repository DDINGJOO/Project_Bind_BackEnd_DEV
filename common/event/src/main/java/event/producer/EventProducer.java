package event.producer;

import event.domain.BaseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 토픽을 파라미터로 받아서 이벤트를 발행합니다.
     */
    public void publishEvent(String topic, BaseEvent event) {
        kafkaTemplate.send(topic, event.getEventType().name(), event);
    }
}
