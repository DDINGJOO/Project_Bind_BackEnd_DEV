package bind.auth;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
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
        "bind.auth",              // 기본 패키지
        "security"                // 공통 security 모듈
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
