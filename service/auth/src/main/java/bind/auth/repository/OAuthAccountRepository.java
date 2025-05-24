package bind.auth.repository;

import bind.auth.entity.OAuthAccount;
import data.enums.auth.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {
    Optional<OAuthAccount> findByProviderAndProviderId(ProviderType provider, String providerId);
}

