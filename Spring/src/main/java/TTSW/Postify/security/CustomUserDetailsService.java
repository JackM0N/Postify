package TTSW.Postify.security;

import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.WebsiteUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final WebsiteUserRepository websiteUserRepository;

    public CustomUserDetailsService(WebsiteUserRepository websiteUserRepository) {
        this.websiteUserRepository = websiteUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<WebsiteUser> user = websiteUserRepository.findByEmail(email);

        if (user.isPresent()) {
            var websiteUser = user.get();
            var authorities = websiteUser.getUserRoles().stream()
                .map(userRole -> "ROLE_" + userRole.getRoleName().name())
                .toList();
            return User.builder()
                .username(websiteUser.getEmail())
                .password(websiteUser.getPassword())
                .authorities(authorities.toArray(new String[0]))
                .build();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
