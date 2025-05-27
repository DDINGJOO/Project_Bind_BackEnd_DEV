package bind.mail;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;


@SpringBootApplication(
        exclude = {SecurityAutoConfiguration.class}
)
@ComponentScan(basePackages = {
        "bind.mail",              // 기본 패키지
        "security"                // 공통 security 모듈
})
@EnableKafka
public class MailApplication {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .filename(".env.dev") // 또는 .env.prod
                .load();

        // 환경 변수에 주입
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        org.springframework.boot.SpringApplication.run(MailApplication.class, args);
    }
}
