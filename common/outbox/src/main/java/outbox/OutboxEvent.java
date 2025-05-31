package outbox;


import event.constant.EventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;



    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String payloadJson;  // Event 전체를 JSON으로 저장

    @Column(nullable = false)
    private String topic;  // Kafka 토픽 이름

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean published = false;
}
