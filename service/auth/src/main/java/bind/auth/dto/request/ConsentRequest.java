package bind.auth.dto.request;

import lombok.Builder;

import java.sql.ConnectionBuilder;


@Builder
public record ConsentRequest(
        String consentType,
        String agreementVersion

) {

}
