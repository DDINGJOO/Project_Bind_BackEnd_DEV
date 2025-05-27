package bind.mail.consumer;


import bind.mail.service.MailService;

import event.constant.EventType;

import event.events.Event;
import event.payload.EmailVerificationEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailConsumer {

    private final MailService mailService;

    @KafkaListener(
            topics = "mails-topic",
            groupId = "mails-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, Event<EmailVerificationEventPayload>> record, Acknowledgment ack) {
        Event<EmailVerificationEventPayload> event = record.value();
        log.info("✅ 이메일 이벤트 수신: {}", event.getPayload().getEmail());

        // 메일 발송 처리
        mailService.sendVerificationEmail(event.getPayload());

        // 메시지 수동 커밋
        ack.acknowledge();
    }
}
