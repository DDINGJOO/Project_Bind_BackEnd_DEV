package bind.image.service.eventPub;


import bind.image.dto.response.ImageResponse;
import event.constant.EventType;
import event.domain.Event;
import event.dto.UserProfileImageUpdateEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import outbox.OutboxPublisher;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageFileEventPub {
    private final OutboxPublisher outboxService;


    public void UserProfileImageUpdateEventPub(ImageResponse response) {
        UserProfileImageUpdateEventPayload payload = new UserProfileImageUpdateEventPayload(
                response.getReferenceId(),
                response.getUrl()
        );

        Event<UserProfileImageUpdateEventPayload> event = new Event<>(
                EventType.USER_PROFILE_IMAGE_UPDATE,
                System.currentTimeMillis(),
                payload
        );

        outboxService.saveEvent("user-profile-image-update-topic", event);
    }


}


/*

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




*/
