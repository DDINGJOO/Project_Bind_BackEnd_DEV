package security.exception;


import exception.BaseException;
import exception.ErrorCode;
import lombok.Getter;

public class SecurityException extends BaseException {
    private final SecurityErrorCode errorCode;

    public SecurityException(String message, SecurityErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }


    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
