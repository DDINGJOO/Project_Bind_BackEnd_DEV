package bind.userInfo;


import io.github.cdimascio.dotenv.Dotenv;
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
        "bind.userInfo",              // 기본 패키지
        "security",                // 공통 security 모듈
        "logging",
        "event",                // 이벤트 모듈
        "data",                 // 공통 데이터 모듈\
        "outbox",                // Outbox 모듈
        "util"
})
@EnableJpaRepositories(basePackages = {
        "bind.userInfo.repository",   // 이미지 도메인 JPA 레포지토리
        "outbox",                  // 반드시 outbox 패키지 JPA 리포지토리!
        // 필요하다면 다른 repository 패키지도 추가
})
@EntityScan(basePackages = {
        "bind.userInfo.entity",   // <= 반드시 여기에!
        "outbox"              // outbox.Entity도 있으면 같이!
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
