package bind.auth.config;

import bind.auth.service.UserSuspensionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSuspensionScheduler {

    private final UserSuspensionService userSuspensionService;

    /**
     * 매 1시간마다 만료된 정지 자동 해제
     */
    @Scheduled(cron = "0 0 0 * * *") //  매일 자정에 실행
    public void releaseExpiredSuspensions() {
        int count = userSuspensionService.releaseExpiredSuspensions();
        if (count > 0) {
            log.info("만료된 정지 {}건 자동 해제 처리됨", count);
        }
    }
}
