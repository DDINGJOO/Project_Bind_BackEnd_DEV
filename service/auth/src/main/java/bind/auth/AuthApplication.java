package bind.auth;


import event.config.KafkaEventConfig;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import security.jwt.JwtProperties;


@EnableScheduling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableConfigurationProperties(JwtProperties.class)
@Import(KafkaEventConfig.class)
@ComponentScan(basePackages = {
        "bind.auth",              // 기본 패키지
        "security",                // 공통 security 모듈
        "logging",
        "event",                // 이벤트 모듈
        "data",                 // 공통 데이터 모듈
        "outbox",                // Outbox 모듈
        "util"                  // 유틸리티 모듈
})
@EnableJpaRepositories(basePackages = {
        "bind.auth.repository",   // 이미지 도메인 JPA 레포지토리
        "outbox",                  // 반드시 outbox 패키지 JPA 리포지토리!
        // 필요하다면 다른 repository 패키지도 추가
})
@EntityScan(basePackages = {
        "bind.auth.entity",   // <= 반드시 여기에!
        "outbox"              // outbox.Entity도 있으면 같이!
})
public class AuthApplication {

    public static void main(String[] args) {
        // .env.dev 또는 .env.prod 로드
        Dotenv dotenv = Dotenv.configure()
                .filename(".env.dev") // 또는 .env.prod
                .load();

        // 환경 변수에 주입
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );



        SpringApplication.run(AuthApplication.class, args);
    }

}
