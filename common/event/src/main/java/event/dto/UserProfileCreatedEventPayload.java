package event.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import event.domain.EventPayload;
import lombok.*;

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
/*
public class EmailVerificationEventPayload implements EventPayload {
    private String referenceId;
    private String token;
    private String email;
}

 */
