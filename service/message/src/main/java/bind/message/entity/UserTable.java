package bind.message.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_table")
public class UserTable {

    @Id
    String userId;
    String profileUrl;
    String nickName;
}

