package bind.userInfo.entity;

import jakarta.persistence.Id;

public class UserBasicProfile {

    @Id
    String userId;


    String userName;
    String userEmail;
    String userPhone;
}
