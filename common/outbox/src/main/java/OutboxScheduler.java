import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxMessageRepository outboxRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxService outboxService;

    @Scheduled(fixedDelay = 5000)
    public void publishOutboxMessages() {
        List<OutboxMessage> messages = outboxRepo.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.READY);
        for (OutboxMessage msg : messages) {
            try {
                kafkaTemplate.send(msg.getTopic(), msg.getMessageKey(), msg.getPayload());
                outboxService.markAsSent(msg);
            } catch (Exception e) {
                outboxService.markAsFailed(msg);
            }
        }
    }
}
