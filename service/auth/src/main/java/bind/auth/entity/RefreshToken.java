package bind.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    private String token;
    private String deviceId;
    private String clientIp;
    private String userAgent;
    private LocalDateTime expiry;
    private LocalDateTime issuedAt;

    public void update(String token, LocalDateTime issuedAt, LocalDateTime expiry) {
        this.token = token;
        this.issuedAt = issuedAt;
        this.expiry = expiry;
    }
}
