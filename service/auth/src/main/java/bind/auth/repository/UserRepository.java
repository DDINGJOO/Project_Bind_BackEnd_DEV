package bind.auth.repository;

import bind.auth.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

}
