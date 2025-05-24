package bind.auth;

import data.enums.auth.UserRoleType;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
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
