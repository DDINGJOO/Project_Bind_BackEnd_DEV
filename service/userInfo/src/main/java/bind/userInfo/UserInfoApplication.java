package bind.userInfo;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import security.jwt.JwtProperties;

@EnableScheduling

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableConfigurationProperties(JwtProperties.class)
@ComponentScan(basePackages = {
        "bind.userInfo",              // 기본 패키지
        "security",                // 공통 security 모듈
        "logging",
        "event",                // 이벤트 모듈
        "data",                 // 공통 데이터 모듈\
        "outbox"                // Outbox 모듈
})
public class UserInfoApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .filename(".env.dev") // 또는 .env.prod
                .load();

        // 환경 변수에 주입
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
        org.springframework.boot.SpringApplication.run(UserInfoApplication.class, args);
    }
}
