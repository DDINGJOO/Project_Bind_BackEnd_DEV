package bind.userInfo.service;

import bind.userInfo.entity.UserProfile;
import event.dto.EmailVerificationEvent;
import event.dto.ProfileCreatedEvent;
import event.dto.UserRegisteredEvent;
import event.dto.UserWithdrawEvent;
import event.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import outbox.OutboxService;
import security.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
@Slf4j

public class EventPubService {
    private final OutboxService outboxService;


    public void userProfileCreatedEvent(String userId) {
        log.info("called userProfileCreatedEvent");

        ProfileCreatedEvent event = new ProfileCreatedEvent(userId);

        // outboxService.saveMessage(토픽명, 키, 이벤트객체);
        outboxService.saveMessage(
                "user-profile-created-topic",  // topic
                userId,                       // key (PK나 고유값)
                event                         // payload (자동 직렬화)
        );
    }

}
