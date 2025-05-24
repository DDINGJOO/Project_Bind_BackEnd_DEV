package bind.auth.entity;

import data.enums.auth.WithdrawType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "withdraw_history")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class WithdrawHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String reason;

    @Enumerated(EnumType.STRING)
    private WithdrawType withdrawType;

    private LocalDateTime withdrawAt;
}
