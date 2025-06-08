package bind.message.exception;


import exception.BaseException;
import exception.ErrorCode;

public class MessageException extends BaseException {

    private final MessageErrorCode errorCode;

    public MessageException(MessageErrorCode c) {
        super(c.getMessage());
        this.errorCode = c;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
