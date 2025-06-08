package event.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import event.domain.EventPayload;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeName("ProfileUpdatedEventPayload")
public class UserProfileUpdatedEventPayload implements EventPayload {
    private String userId;
    private String profileImageUrl;
    private String nickname;

}
