
public class OutboxException extends RuntimeException {
    public OutboxException(String message, Throwable cause) {
        super(message, cause);
    }
}
