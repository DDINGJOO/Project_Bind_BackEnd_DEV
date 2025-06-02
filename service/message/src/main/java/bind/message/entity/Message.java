package bind.message.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderId;
    private boolean isSenderDelete;

    private String subject;

    private String receiverId;
    private boolean isReceiverDelete;

    private String contents;

    private LocalDateTime createdAt;


    private boolean isOpen = false;
}
