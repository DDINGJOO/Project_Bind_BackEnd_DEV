package bind.auth.service;


import bind.auth.dto.Oauth.OauthUserInfo;
import bind.auth.entity.OAuthAccount;
import bind.auth.entity.User;
import bind.auth.entity.UserRole;
import bind.auth.exception.AuthErrorCode;
import bind.auth.exception.AuthException;
import bind.auth.repository.OAuthAccountRepository;
import bind.auth.repository.UserRepository;
import bind.auth.repository.UserRoleRepository;
import data.enums.auth.UserRoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import util.pkProvider.PkProvider;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OauthService {
    private final OAuthAccountRepository oAuthAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;


    public User saveOAuthAccount(OauthUserInfo user) {
        // OAuth 계정 정보를 저장하는 로직
        // 예: oAuthAccountRepository.save(new OAuthAccount(user.getProvider(), user.getProviderId(), user.getEmail()));

        if(checkOAuthAccountExists(user)) {
           throw new AuthException(AuthErrorCode.EXIST_OAUTH.getMessage(), AuthErrorCode.EXIST_OAUTH);
        }
        User ouser = User.builder()
                .id(PkProvider.getInstance().generate())
                .loginId(PkProvider.getInstance().generate())
                .email(user.getEmail())
                .provider(user.getProvider())
                .isEmailVerified(true)
                .isActive(true)
                .isSocialOnly(true)
                .createdAt(LocalDateTime.now())
                .build();


        oAuthAccountRepository.save(
                OAuthAccount.builder()
                        .connectedAt(LocalDateTime.now())
                        .user(ouser)
                        .email(user.getEmail())
                        .isConnected(true)
                        .build()
        );

        userRepository.save(ouser);

        userRoleRepository.save(
                UserRole.builder()
                        .user(ouser)
                        .role(UserRoleType.USER)
                        .build()
        );


        return ouser;
    }


    private boolean checkOAuthAccountExists(OauthUserInfo user) {
        // OAuth 계정이 이미 존재하는지 확인하는 로직
        // 예: oAuthAccountRepository.existsByProviderAndProviderId(provider, providerId);

        if( oAuthAccountRepository.existsByProviderAndProviderId(user.getProvider(), user.getProviderId()))
        {
            return true;
        }

        return false;
    }

}
