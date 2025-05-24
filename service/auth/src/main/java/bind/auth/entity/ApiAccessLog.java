package bind.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_access_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiAccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String method;
    private String path;
    private int status;
    private int responseTimeMs;
    private String ipAddress;
    private LocalDateTime requestedAt;
}
