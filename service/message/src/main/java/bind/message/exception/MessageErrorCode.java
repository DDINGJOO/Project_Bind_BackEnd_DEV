package bind.message.exception;


import exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum MessageErrorCode implements ErrorCode {
    MESSAGE_NOT_FOUND(404, "Message not found", "MESSAGE_NOT_FOUND"),
    MESSAGE_SEND_FAILED(500, "Failed to send message", "MESSAGE_SEND_FAILED"),
    MESSAGE_RECEIVER_NOT_FOUND(404, "Receiver not found", "MESSAGE_RECEIVER_NOT_FOUND"),
    MESSAGE_SENDER_NOT_FOUND(404, "Sender not found", "MESSAGE_SENDER_NOT_FOUND"),
    MESSAGE_ALREADY_EXISTS(409, "Message already exists", "MESSAGE_ALREADY_EXISTS"),
    MESSAGE_INVALID_REQUEST(400, "Invalid message request", "MESSAGE_INVALID_REQUEST"),
    MESSAGE_SENDER_NOT_MATCH(403, "Sender does not match", "MESSAGE_SENDER_NOT_MATCH"),
    MESSAGE_ALREADY_DELETED_BY_SENDER(
            400, "Message already deleted by sender", "MESSAGE_ALREADY_DELETED_BY_SENDER"),


    MESSAGE_RECEIVER_NOT_MATCH(
            403, "Receiver does not match", "MESSAGE_RECEIVER_NOT_MATCH"),

    MESSAGE_ALREADY_DELETED_BY_RECEIVER(
            400, "Message already deleted by receiver", "MESSAGE_ALREADY_DELETED_BY_RECEIVER"),
    MESSAGE_USER_NOT_MATCH(
            403, "User does not match with message", "MESSAGE_USER_NOT_MATCH"
    );




    private final int status;
    private final String message;
    private final String code;
}
