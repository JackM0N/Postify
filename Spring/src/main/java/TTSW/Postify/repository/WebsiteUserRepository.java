package TTSW.Postify.repository;

import TTSW.Postify.model.WebsiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WebsiteUserRepository extends JpaRepository<WebsiteUser, Long>, JpaSpecificationExecutor<WebsiteUser> {
    Optional<WebsiteUser> findByEmail(String email);
    Optional<WebsiteUser> findByUsername(String username);
}
