package bind.auth.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_suspension")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Builder
public class UserSuspension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String reason;

    @Column(name = "suspended_at", nullable = false)
    private LocalDateTime suspendedAt;

    @Column(name = "release_at")
    private LocalDateTime releaseAt; // null이면 영구정지

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public boolean isCurrentlySuspended() {
        if (!isActive) return false;
        return releaseAt == null || releaseAt.isAfter(LocalDateTime.now());
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }


}
