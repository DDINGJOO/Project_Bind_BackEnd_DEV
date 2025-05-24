package bind.auth.entity;

import data.enums.auth.ConsentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consent_history")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ConsentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ConsentType consentType;

    private boolean agreed;
    private LocalDateTime agreedAt;
    private String agreementVersion;
    private String source;
}
