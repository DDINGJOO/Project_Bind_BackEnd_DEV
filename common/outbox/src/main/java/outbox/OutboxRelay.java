package outbox;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import event.domain.Event;
import event.domain.EventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelay {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, Event<? extends EventPayload>> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void relay() {
        List<OutboxEvent> events = repository.findTop100ByPublishedIsFalseOrderByCreatedAtAsc();

                for (OutboxEvent outbox : events) {
                    try {
                        Event<? extends EventPayload> event = objectMapper.readValue(outbox.getPayloadJson(), Event.class);
                        kafkaTemplate.send(outbox.getTopic(), event);
                        outbox.setPublished(true);
                        repository.save(outbox);
                    } catch (JsonProcessingException e) {
                        log.error("Outbox 역직렬화 실패", e);
                    }
        }
    }
}
