package bind.userInfo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_interest")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId; // FK: user_profile.userId

    @Column(nullable = false, length = 32)
    private String interest; // ex: "Jazz", "React", "드럼"

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
