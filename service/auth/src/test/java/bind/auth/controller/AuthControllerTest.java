package bind.auth.controller;

import bind.auth.dto.request.LoginRequest;
import bind.auth.dto.request.RegisterRequest;
import bind.auth.dto.response.LoginResponse;
import data.BaseResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + 9000 + path;
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        RegisterRequest request = new RegisterRequest("newUserId", "password123");

        ResponseEntity<BaseResponse> response = restTemplate.postForEntity(
                url("/api/auth/register"),
                request,
                BaseResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // 사전 회원가입
        restTemplate.postForEntity(
                url("/api/auth/register"),
                new RegisterRequest("loginUser", "pw1234"),
                BaseResponse.class
        );

        LoginRequest loginRequest = new LoginRequest("loginUser", "pw1234", "device-001");

        ResponseEntity<BaseResponse> response = restTemplate.postForEntity(
                url("/api/auth/login"),
                loginRequest,
                BaseResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
    }

    @Test
    @DisplayName("잘못된 비밀번호 로그인 실패")
    void login_fail_invalidPassword() {
        restTemplate.postForEntity(
                url("/api/auth/register"),
                new RegisterRequest("wrongPwUser", "realpw123"),
                BaseResponse.class
        );

        LoginRequest loginRequest = new LoginRequest("wrongPwUser", "wrongpw", "dev");

        ResponseEntity<BaseResponse> response = restTemplate.postForEntity(
                url("/api/auth/login"),
                loginRequest,
                BaseResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
    }
}
