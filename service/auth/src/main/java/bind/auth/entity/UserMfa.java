package bind.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_mfa")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMfa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String mfaType;
    private LocalDateTime registeredAt;
    private boolean verified;
}
