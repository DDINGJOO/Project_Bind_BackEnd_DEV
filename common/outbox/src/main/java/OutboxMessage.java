import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_message")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String topic;

    @Column(nullable = false, length = 64)
    private String messageKey;

    @Lob
    @Column(nullable = false)
    private String payload;  // 직렬화된 JSON 등

    @Column(nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;   // READY, SENT, FAILED

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime sentAt;
}
