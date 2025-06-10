package bind.userInfo.service;

import bind.userInfo.dto.response.UserProfileSummaryResponse;
import event.constant.EventType;
import event.domain.Event;
import event.dto.UserProfileCreatedEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import outbox.OutboxPublisher;

@Service
@RequiredArgsConstructor
@Slf4j

public class EventPubService {


    private final OutboxPublisher outboxService;


    /**
     * 이메일 인증 이벤트를 카프카에 발행합니다.
     *
     */
    public void userProfileCreatedEventPub(UserProfileSummaryResponse userProfile) {
        log.info("called userProfileCreatedEventPub");


        UserProfileCreatedEventPayload payload = new UserProfileCreatedEventPayload(
                userProfile.getUserId(),
                userProfile.getProfileImageUrl(),
                userProfile.getNickname()
        );

        Event<UserProfileCreatedEventPayload> event = new Event<>(
                EventType.USER_PROFILE_CREATED,
                System.currentTimeMillis(),
                payload
        );

        outboxService.saveEvent("user-profile-created-topic", event);
    }


    public void userProfileUpdatedEventPub(UserProfileSummaryResponse userProfile) {
        log.info("called userProfileUpdatedEventPub");


        UserProfileCreatedEventPayload payload = new UserProfileCreatedEventPayload(
                userProfile.getUserId(),
                userProfile.getProfileImageUrl(),
                userProfile.getNickname()
        );

        Event<UserProfileCreatedEventPayload> event = new Event<>(
                EventType.USER_PROFILE_NICKNAME_UPDATED,
                System.currentTimeMillis(),
                payload
        );

        outboxService.saveEvent("user-profile-updated-topic", event);
    }





}
