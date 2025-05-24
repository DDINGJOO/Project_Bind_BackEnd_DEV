
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    enum DummyErrorCode implements ErrorCode {
        EXAMPLE_ERROR("DUMMY_001", "예시 에러입니다", 400);

        private final String code;
        private final String message;
        private final int status;

        DummyErrorCode(String code, String message, int status) {
            this.code = code;
            this.message = message;
            this.status = status;
        }

        @Override public String getCode() { return code; }
        @Override public String getMessage() { return message; }
        @Override public int getStatus() { return status; }
    }

    @Test
    @DisplayName("ErrorCode에서 ErrorResponse 생성 테스트")
    void createErrorResponseFromErrorCode() {
        ErrorResponse response = ErrorResponse.from(DummyErrorCode.EXAMPLE_ERROR);

        assertThat(response.code()).isEqualTo("DUMMY_001");
        assertThat(response.message()).isEqualTo("예시 에러입니다");
    }
}
