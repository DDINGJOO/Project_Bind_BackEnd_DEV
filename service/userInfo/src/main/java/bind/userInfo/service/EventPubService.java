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
import security.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
@Slf4j

public class EventPubService {

    private  final JwtProvider tokenProvider;
    private final EventProducer eventProducer;
    private final UserProfileService userProfileService;

    /**
     * 이메일 인증 이벤트를 카프카에 발행합니다.
     *
     */
    public void kafkaUserProfileCreated(String userId, String profileImageId, String nickname) {
        log.info("called kafkaUserProfileCreated");
        eventProducer.publishEvent("user-profile-created-topic",
                new ProfileCreatedEvent(userId, profileImageId, nickname)
        );
    }




}
