package bind.image.consumer;

import bind.image.service.ImageFileService;
import event.constant.EventType;
import event.domain.Event;
import event.dto.ProfileUpdatedEventPayload;
import event.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileUpdatedEventConsumer implements EventHandler<ProfileUpdatedEventPayload> {

    private final ImageFileService imageFileService;

    @Override
    public EventType supportedType() {
        return EventType.USER_PROFILE_UPDATED;
    }

    @Override
    public void handle(Event<ProfileUpdatedEventPayload> event) {
        ProfileUpdatedEventPayload payload = event.getPayload();

        if (payload == null || payload.getImageId() == null) {
            log.warn("ProfileUpdatedEventPayload is null or missing imageId. Skipping.");
            return;
        }

        log.info("Processing profile image update for imageId: {}", payload.getImageId());

        imageFileService.confirmImage(payload.getImageId());
    }
}
