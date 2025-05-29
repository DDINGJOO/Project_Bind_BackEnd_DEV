package bind.auth.controller;


import event.constant.EventType;

import event.dto.UserRegisteredEvent;
import event.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController()
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


}
