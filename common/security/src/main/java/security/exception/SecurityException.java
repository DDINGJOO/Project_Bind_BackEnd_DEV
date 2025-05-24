package security.exception;


import lombok.Getter;

public class SecurityException extends RuntimeException {
    private final SecurityErrorCode errorCode;

    public SecurityException(SecurityErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public SecurityErrorCode getErrorCode() {
        return errorCode;
    }
}
