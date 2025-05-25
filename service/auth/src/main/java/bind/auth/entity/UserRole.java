package bind.auth.entity;

import data.enums.auth.UserRoleType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserRoleId.class)
public class UserRole {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @Enumerated(EnumType.STRING)
    private UserRoleType role;

    private LocalDateTime grantedAt;

    private String grantedBy;// "부여자 UUID"를 의미합니다. 이 필드는 사용자가 역할을 부여받은 사람의 UUID를 저장합니다.
}

