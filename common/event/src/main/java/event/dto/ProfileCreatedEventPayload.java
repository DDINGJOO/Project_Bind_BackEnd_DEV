package event.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import event.constant.EventType;
import event.domain.EventPayload;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonTypeName("ProfileCreatedEventPayload")
public class ProfileCreatedEventPayload implements EventPayload {
    private String userId;

}
