package bind.auth.repository;

import bind.auth.entity.User;
import bind.auth.entity.UserRole;
import bind.auth.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    Optional<UserRole>  findByUserId(String id);
}

