package bind.image;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import security.jwt.JwtProperties;

@EnableScheduling

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableConfigurationProperties(JwtProperties.class)
@ComponentScan(basePackages = {
        "bind.image",              // 기본 패키지
        "security",                // 공통 security 모듈
        "logging",
        "event",                // 이벤트 모듈
        "outbox",                // Outbox 모듈
})

@EnableJpaRepositories(basePackages = {
        "bind.image.repository",   // 이미지 도메인 JPA 레포지토리
        "outbox",                  // 반드시 outbox 패키지 JPA 리포지토리!

})
@EntityScan(basePackages = {
        "bind.image.entity",   // <= 반드시 여기에!
        "outbox"              // outbox.Entity도 있으면 같이!
})
public class ImageApplication {
    public static void main(String[] args) {
        // .env.dev 또는 .env.prod 로드
        Dotenv dotenv = Dotenv.configure()
                .filename(".env.dev") // 또는 .env.prod
                .load();

        // 환경 변수에 주입
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );



        SpringApplication.run(ImageApplication.class, args);
    }
}
