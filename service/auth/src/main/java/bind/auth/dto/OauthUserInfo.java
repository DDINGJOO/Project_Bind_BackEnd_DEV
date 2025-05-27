package bind.auth.dto;


import data.enums.auth.ProviderType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class OauthUserInfo {
    ProviderType provider;
    String providerId;
    String email;

}
