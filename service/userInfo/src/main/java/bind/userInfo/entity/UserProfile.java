package bind.userInfo.entity;

import data.enums.location.Location;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "profile_image_url", length = 200)
    private String profileImageUrl; // image-service 연동

    @Column(length = 200)
    private String introduction;

    @Column(length = 64)
    private String interests; // 관심사(태그/쉼표 구분), 별도 테이블 분리도 가능

    @Column(length = 8)
    private String gender;

    @Column
    private LocalDateTime birthdate;

    @Enumerated(EnumType.STRING)
    private Location location;

    @Column
    private LocalDateTime lastActiveAt;

    @Column
    private Boolean profilePublic;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userId")
    private List<UserInterest> userInterests;
}
