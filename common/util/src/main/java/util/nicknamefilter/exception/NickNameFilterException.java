package util.nicknamefilter.exception;


import exception.BaseException;
import exception.ErrorCode;

public class NickNameFilterException extends BaseException {

    private final NickNameFilterErrorCode errorCode;

    public NickNameFilterException(NickNameFilterErrorCode c) {
        super(c.getMessage());
        this.errorCode = c;
    }

    public NickNameFilterException(ErrorCode c, String message) {
        super(message);
        this.errorCode = (NickNameFilterErrorCode) c;
    }


    @Override
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
