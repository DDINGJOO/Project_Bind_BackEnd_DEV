import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxMessageRepository outboxRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveMessage(String topic, String key, Object payloadObj) {
        try {
            String payload = objectMapper.writeValueAsString(payloadObj);
            OutboxMessage msg = OutboxMessage.builder()
                    .topic(topic)
                    .messageKey(key)
                    .payload(payload)
                    .status(OutboxStatus.READY)
                    .createdAt(LocalDateTime.now())
                    .build();
            outboxRepo.save(msg);
        } catch (Exception e) {
            throw new OutboxException("메시지 직렬화 실패", e);
        }
    }

    @Transactional
    public void markAsSent(OutboxMessage msg) {
        msg.setStatus(OutboxStatus.SENT);
        msg.setSentAt(LocalDateTime.now());
        outboxRepo.save(msg);
    }

    @Transactional
    public void markAsFailed(OutboxMessage msg) {
        msg.setStatus(OutboxStatus.FAILED);
        outboxRepo.save(msg);
    }
}
