package bind.auth.repository;

import bind.auth.entity.UserRole;
import bind.auth.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}

