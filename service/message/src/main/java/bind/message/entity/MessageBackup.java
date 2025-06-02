package bind.message.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "message_backup",
        indexes = {
                @Index(name = "idx_message_receiverId_createdAt", columnList = "receiverId, createdAt"),
                @Index(name = "idx_message_senderId_createdAt", columnList = "senderId, createdAt")
        }
)
@AllArgsConstructor
@Builder
@NoArgsConstructor

public class MessageBackup {
    @Id
    private Long id;    // 원본 PK 그대로

    @Column(nullable = false)
    private String senderId;
    @Column(nullable = false)
    private String receiverId;
    @Column(nullable = false, length = 200)
    private String subject;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime senderDeletedAt;
    private LocalDateTime receiverDeletedAt;

    // 추가: 삭제 이관 시점, 삭제 요청자, 삭제 요청 IP 등
    private LocalDateTime movedAt;


}
