package bind.message.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profile_snapshot")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileSnapshot {

    @Id
    String userId;
    String profileUrl;
    String nickName;

    Long unReadMessageCount;
    Long sendMessageCount;
    Long receiveMessageCount;
}

