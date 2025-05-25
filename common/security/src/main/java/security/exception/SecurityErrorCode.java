package security.exception;

import exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {
    // 인증 관련
    TOKEN_NOT_FOUND("SEC_001", "토큰이 존재하지 않습니다.", 401),
    TOKEN_INVALID("SEC_002", "유효하지 않은 토큰입니다.", 401),
    TOKEN_EXPIRED("SEC_003", "토큰이 만료되었습니다.", 401),
    PERMISSION_DENIED("SEC_004", "권한이 없습니다.", 403),
    USER_NOT_AUTHENTICATED("SEC_005", "사용자가 인증되지 않았습니다.", 401),
    USER_NOT_FOUND("SEC_006", "사용자를 찾을 수 없습니다.", 404),
    USER_DEACTIVATED("SEC_007", "사용자가 비활성화되었습니다.", 403),
    USER_SUSPENDED("SEC_008", "사용자가 정지되었습니다.", 403),
    // 인가 관련
    ROLE_NOT_FOUND("SEC_009", "역할을 찾을 수 없습니다.", 404),
    ROLE_ALREADY_EXISTS("SEC_010", "역할이 이미 존재합니다.", 409),
    // 기타
    INTERNAL_ERROR("SEC_999", "알 수 없는 보안 오류가 발생했습니다.", 500);


    private final String code;
    private final String message;
    private final int status;
}
