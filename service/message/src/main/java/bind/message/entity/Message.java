package bind.message.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(
        name = "message",
        indexes = {
                @Index(name = "idx_message_receiverId_createdAt", columnList = "receiverId, createdAt"),
                @Index(name = "idx_message_senderId_createdAt", columnList = "senderId, createdAt")
        }
)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외래키 참조 (User 엔티티 필요)
    @Column(nullable = false)
    private String senderId;      // FK -> User
    @Column(nullable = false)
    private String receiverId;    // FK -> User

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // soft delete timestamp
    private LocalDateTime senderDeletedAt;
    private LocalDateTime receiverDeletedAt;

    // 읽음 처리
    private LocalDateTime readAt;   // null이면 미열람

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
