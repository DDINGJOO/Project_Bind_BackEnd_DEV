package bind.auth.repository;

import bind.auth.entity.RefreshToken;
import bind.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, User> {
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);

    void deleteByUserIdAndDeviceId(String userId, String deviceId);
    Optional<RefreshToken> findByUserIdAndDeviceId(String userId, String deviceId);

    boolean existsByUserIdAndDeviceId(String userId, String deviceId);
}
