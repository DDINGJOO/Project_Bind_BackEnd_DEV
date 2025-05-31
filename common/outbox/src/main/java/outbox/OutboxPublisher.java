package outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import event.domain.Event;
import event.domain.EventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveEvent(String topic, Event<? extends EventPayload> event){
        try {{
                String payloadJson = objectMapper.writeValueAsString(event);
                OutboxEvent outboxEvent = OutboxEvent.builder()
                        .eventType(event.getType())
                        .topic(topic)   //
                        .payloadJson(payloadJson)
                        .createdAt(LocalDateTime.now())
                        .build();

                repository.save(outboxEvent);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Outbox 직렬화 실패", e);
        }
    }
}
