package util.nicknamefilter.exception;


import exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum NickNameFilterErrorCode implements ErrorCode {
    NICKNAME_CONTAINS_BAD_WORD(400, "Nickname contains a bad word", "NICKNAME_CONTAINS_BAD_WORD"),
    NICKNAME_TOO_SHORT(400, "Nickname must be at least 2 characters long", "NICKNAME_TOO_SHORT"),
    NICKNAME_TOO_LONG(400, "Nickname must be at most 20 characters long", "NICKNAME_TOO_LONG"),
    NICKNAME_INVALID_CHARACTERS(400, "Nickname can only contain letters, numbers, and underscores", "NICKNAME_INVALID_CHARACTERS"),
    NICKNAME_STARTS_OR_ENDS_WITH_UNDERSCORE(400, "Nickname cannot start or end with an underscore", "NICKNAME_STARTS_OR_ENDS_WITH_UNDERSCORE"),
    NICKNAME_CONTAINS_CONSECUTIVE_UNDERSCORES(400, "Nickname cannot contain consecutive underscores", "NICKNAME_CONTAINS_CONSECUTIVE_UNDERSCORES"),
    NICKNAME_CONTAINS_SPACES(400, "Nickname cannot contain spaces", "NICKNAME_CONTAINS_SPACES"),
    NICKNAME_CONTAINS_SPECIAL_CHARACTERS(400, "Nickname cannot contain special characters", "NICKNAME_CONTAINS_SPECIAL_CHARACTERS"),
    NICKNAME_ALREADY_EXISTS(409, "Nickname already exists", "NICKNAME_ALREADY_EXISTS"),
    NICKNAME_TOO_MANY_BAD_WORDS(400, "Nickname contains too many bad words", "NICKNAME_TOO_MANY_BAD_WORDS"),
    NICKNAME_NOT_PROVIDED(400, "Nickname must be provided", "NICKNAME_NOT_PROVIDED"),
    NICKNAME_EMPTY(400, "Nickname cannot be empty", "NICKNAME_EMPTY"),
    ;


    private final int status;
    private final String message;
    private final String code;
}
