package bind.userInfo.exception;


import exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ProfileErrorCode implements ErrorCode {
    USER_NOT_FOUND(404, "User not found", "USER_NOT_FOUND"),
    PROFILE_NOT_FOUND(404, "Profile not found", "PROFILE_NOT_FOUND"),
    PROFILE_UPDATE_FAILED(500, "Profile update failed", "PROFILE_UPDATE_FAILED"),
    PROFILE_DELETE_FAILED(500, "Profile delete failed", "PROFILE_DELETE_FAILED"),
    PROFILE_PICTURE_UPLOAD_FAILED(500, "Profile picture upload failed", "PROFILE_PICTURE_UPLOAD_FAILED"),
    PROFILE_PICTURE_DELETE_FAILED(500, "Profile picture delete failed", "PROFILE_PICTURE_DELETE_FAILED"),
    PROFILE_PICTURE_NOT_FOUND(404, "Profile picture not found", "PROFILE_PICTURE_NOT_FOUND"),
    PROFILE_PICTURE_SIZE_EXCEEDED(400, "Profile picture size exceeded", "PROFILE_PICTURE_SIZE_EXCEEDED"),
    PROFILE_PICTURE_FORMAT_UNSUPPORTED(400, "Profile picture format unsupported", "PROFILE_PICTURE_FORMAT_UNSUPPORTED"),
    NICKNAME_ALREADY_EXISTS(409, "Nickname already exists", "NICKNAME_ALREADY_EXISTS"),
    INVALID_PROFILE_DATA(400, "Invalid profile data", "INVALID_PROFILE_DATA"),
    PROFILE_ALREADY_EXISTS(409, "Profile already exists", "PROFILE_ALREADY_EXISTS"),
    ;


    private final int status;
    private final String message;
    private final String code;
}
