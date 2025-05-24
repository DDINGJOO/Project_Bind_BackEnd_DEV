package data.enums.event;

public interface EventType {
    String USER_LOGIN_ATTEMPT = "user.login.attempt";
    String USER_WITHDRAW = "user.withdraw";
    String USER_PASSWORD_CHANGED = "user.password.changed";
    // 추후 COMMENT 관련 이벤트 등 추가 가능
}
