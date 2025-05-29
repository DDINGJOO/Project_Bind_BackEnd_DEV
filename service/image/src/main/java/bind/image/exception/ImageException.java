package bind.image.exception;

import exception.BaseException;
import exception.ErrorCode;

public class ImageException extends BaseException {

    private final ImageErrorCode errorCode;

    public ImageException(ImageErrorCode c) {
        super(c.getMessage());
        this.errorCode = c;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
