package bind.auth.controller.kakao;


import bind.auth.dto.OauthUserInfo;
import bind.auth.dto.kakao.KakaoUserInfoResponseDto;
import bind.auth.dto.response.LoginResponse;
import bind.auth.entity.User;
import bind.auth.service.AuthService;
import bind.auth.service.OauthService;
import bind.auth.service.kako.KaKaoOauthService;
import data.enums.auth.UserRoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import security.jwt.JwtProvider;
import security.jwt.TokenParam;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/callback")
public class KakaoLoginController {

    private final KaKaoOauthService kakaoService;
    private final OauthService oauthService;
    private final JwtProvider jwtProvider;



    @GetMapping("/kako")
    public ResponseEntity<LoginResponse> callback(
            @RequestParam("code") String code){


        String kakaoAccessToken = kakaoService.getAccessTokenFromKakao(code);
        OauthUserInfo kakaoUserInfo = kakaoService.getUserInfo(kakaoAccessToken);
        User user = oauthService.saveOAuthAccount(kakaoUserInfo);


        return ResponseEntity.ok(
                LoginResponse.builder()
                        .accessToken(jwtProvider.createAccessToken(new TokenParam(user.getId(), UserRoleType.USER.name())))
                        .refreshToken(jwtProvider.createRefreshToken(new TokenParam(user.getId(), UserRoleType.USER.name())))
                        .build()
        );
    }
}
