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
@JsonTypeName("UserProfileImageUpdateEventPayload")
public class UserProfileImageUpdateEventPayload implements EventPayload {
    private String userId;
    private String profileImageUrl;
}


/*

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeName("ProfileCreatedEventPayload")
public class UserProfileCreatedEventPayload implements EventPayload {
    private String userId;
    private String profileImageUrl;
    private String nickname;

}
 */
