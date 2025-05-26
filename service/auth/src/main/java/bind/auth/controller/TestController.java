package bind.auth.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @GetMapping("/ping")
    public String ping() {
        log.info("Ping test called");
        System.out.println("ddd");
        return "pong";
    }

    @PostConstruct
    public void testLog() {
        log.info("✅ SLF4J 테스트 로그입니다.");
        System.out.println("✅ System.out.println 테스트");
    }
}
