package bind.auth.exception;

import exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    // 로그인 관련
    USER_NOT_FOUND("AUTH_001", "존재하지 않는 사용자입니다.", 404),
    PASSWORD_NOT_MATCHED("AUTH_002", "비밀번호가 일치하지 않습니다.", 401),
    DEACTIVATED_USER("AUTH_003", "비활성화된 사용자입니다.", 403),
    SUSPENDED_USER("AUTH_004", "정지된 사용자입니다.", 403),

    CURRENT_PASSWORD_MATCHED("AUTH_011", "현재 비밀번호와 일치합니다.", 400),

    // 토큰 관련
    TOKEN_INVALID("AUTH_005", "유효하지 않은 토큰입니다.", 401),
    TOKEN_EXPIRED("AUTH_006", "만료된 토큰입니다.", 401),
    INVALID_REFRESH_TOKEN("AUTH_007", "유효하지 않은 리프레시 토큰입니다.", 401),

    // 회원가입 관련
    DUPLICATE_LOGIN_ID("AUTH_008", "이미 사용 중인 로그인 ID입니다.", 409),

    // 블록 관련
    BLOCK_ALREADY_EXISTS("AUTH_009", "이미 차단한 사용자입니다.", 400),
    BLOCK_NOT_FOUND("AUTH_010", "차단 내역이 존재하지 않습니다.", 404),

    // 기타
    INTERNAL_ERROR("AUTH_999", "알 수 없는 인증 오류가 발생했습니다.", 500),
    USER_ROLE_NOT_FOUND("AUTH_012", "사용자 역할을 찾을 수 없습니다.", 404);

    private final String code;
    private final String message;
    private final int status;
}
