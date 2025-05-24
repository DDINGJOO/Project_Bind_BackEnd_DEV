package bind.auth.entity;

import data.enums.auth.ProviderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    private String password;

    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    private boolean isSocialOnly;
    private boolean isActive = true;
    private int loginFailCount;
    private LocalDateTime accountLockedUntil;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

