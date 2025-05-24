package bind.auth.exception;


import exception.BaseException;
import exception.ErrorCode;

public class AuthException extends BaseException {

    private final AuthErrorCode errorCode;

    public AuthException(String message, AuthErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
