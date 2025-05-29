package bind.image.consumer;

import bind.image.service.ImageFileService;
import event.constant.EventType;
import event.dto.ProfileUpdatedEvent;
import event.dto.UserRegisteredEvent;
import event.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileUpdatedEventConsumer implements EventHandler<ProfileUpdatedEvent> {

    private final ImageFileService imageFileService;



    @Override
    public EventType supportedType() {
        return EventType.PROFILE_UPDATED;
    }


    @Override
    public void handle(ProfileUpdatedEvent event) {
        if(event == null || event.getImageId() == null) {
            return; // 이벤트가 없거나 이미지 ID가 없는 경우 처리하지 않음
        }

        imageFileService.confirmImage(
                event.getImageId()
        );
    }
}

