package data.enums.auth;

public enum WithdrawType {
    USER_REQUESTED("user_requested"),
    ADMIN_REQUESTED("admin_requested"),
    SYSTEM_INITIATED("system_initiated");

    private final String type;

    WithdrawType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
