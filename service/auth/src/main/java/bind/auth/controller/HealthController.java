package bind.auth.controller;


import event.constant.EventType;
import event.events.Event;
import event.payload.EmailVerificationEventPayload;
import event.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/test")
@Slf4j
@RequiredArgsConstructor
public class HealthController {

    private final EventProducer eventProducer;

    @GetMapping("/ping")
    public ResponseEntity<String> checkHealth()
    {
        System.out.println("테스트 API");
        log.info("Test API");
        return ResponseEntity.ok("pong");
    }


    @PostMapping("/event")
    public String publishTestEvent() {
        System.out.println("테스트 이벤트 발행");
        Event<EmailVerificationEventPayload> event = Event.<EmailVerificationEventPayload>builder()
                .type(EventType.EMAIL_VERIFICATION)
                .payload(EmailVerificationEventPayload.builder()
                        .userId("test-user-id") // Replace with actual user ID
                        .email("soh100101@gmail.com") // Replace with actual email
                        .verificationToken("test-verification-token") // Replace with actual token generation logic
                        .build())
                .timestamp(System.currentTimeMillis())
                .build();
        eventProducer.send(event.getType(), event.getPayload(), "mails-topic");
        return "Test event published!";
    }
}
