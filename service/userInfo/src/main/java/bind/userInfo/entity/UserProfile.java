package bind.userInfo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId; // FK: auth의 user.id

    @Column(nullable = false, length = 32)
    private String nickname;

    @Column(name = "profile_image_id", length = 100)
    private String profileImageId; // image-service 연동

    @Column(length = 200)
    private String introduction;

    @Column(length = 64)
    private String interests; // 관심사(태그/쉼표 구분), 별도 테이블 분리도 가능

    @Column(length = 8)
    private String gender;

    @Column
    private LocalDateTime birthdate;

    @Column(length = 64)
    private String location;

    @Column
    private LocalDateTime lastActiveAt;

    @Column
    private Boolean profilePublic;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}
