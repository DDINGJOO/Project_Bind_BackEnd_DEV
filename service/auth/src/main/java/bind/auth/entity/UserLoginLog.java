package bind.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "user_login_log")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserLoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String ipAddress;
    private String userAgent;
    private boolean success;
    private String reason;
    private String geoLocation;
    private LocalDateTime loginAt;
}

