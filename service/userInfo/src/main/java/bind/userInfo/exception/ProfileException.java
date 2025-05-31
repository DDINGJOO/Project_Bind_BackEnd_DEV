package bind.userInfo.exception;


import exception.BaseException;
import exception.ErrorCode;

public class ProfileException extends BaseException {

    private final ProfileErrorCode errorCode;

    public ProfileException(ProfileErrorCode c) {
        super(c.getMessage());
        this.errorCode = c;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
