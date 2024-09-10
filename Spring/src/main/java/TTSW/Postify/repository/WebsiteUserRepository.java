package TTSW.Postify.repository;

import TTSW.Postify.model.WebsiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebsiteUserRepository extends JpaRepository<WebsiteUser, Long> {
    Optional<WebsiteUser> findByEmail(String email);
}
