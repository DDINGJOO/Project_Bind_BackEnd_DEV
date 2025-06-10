package event.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import event.domain.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeName("UserProfileNicknameUpdatedEventPayload")
public class UserProfileUpdatedEventPayload implements EventPayload {
    private String userId;
    private String nickname;

}
