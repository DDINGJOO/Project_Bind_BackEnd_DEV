package security.exception;

import lombok.Getter;

@Getter
public enum SecurityErrorCode {
    TOKEN_EXPIRED("만료된 토큰입니다."),
    TOKEN_INVALID("유효하지 않은 토큰입니다.");

    private final String message;

    SecurityErrorCode(String message) {
        this.message = message;
    }
}
