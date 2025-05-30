package bind.userInfo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(nullable = false, length = 32)
    private String changedField;

    @Column(columnDefinition = "text")
    private String beforeValue;

    @Column(columnDefinition = "text")
    private String afterValue;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @Column(length = 36)
    private String changedBy; // 유저/어드민 id
}
