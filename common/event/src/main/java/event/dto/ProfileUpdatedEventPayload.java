package event.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import event.constant.EventType;
import event.domain.EventPayload;
import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
@JsonTypeName("ProfileUpdatedEventPayload")
public class ProfileUpdatedEventPayload implements EventPayload {
    private String userId;
    private Long imageId;

}
