package bind.userInfo.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile_admin")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileAdmin {
    @Id
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId; // FK: user_profile.userId

    @Column
    private Boolean isHidden;      // 어드민 임시 숨김

    @Column
    private Integer warningCount;  // 경고 횟수

    @Column(length = 200)
    private String adminNote;      // 어드민 메모

    @Column(length = 36)
    private String lastAdminId;    // 마지막 작업 어드민 ID

    @Column
    private LocalDateTime lastAdminUpdatedAt;
}
