package bind.message.repository;

import bind.message.entity.UserTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTableRepository extends JpaRepository<UserTable, String> {


}
