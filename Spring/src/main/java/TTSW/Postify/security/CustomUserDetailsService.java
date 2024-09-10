package TTSW.Postify.security;

import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.model.Role;
import TTSW.Postify.repository.WebsiteUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final WebsiteUserRepository websiteUserRepository;

    public CustomUserDetailsService(WebsiteUserRepository websiteUserRepository) {
        this.websiteUserRepository = websiteUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<WebsiteUser> user = websiteUserRepository.findByEmail(email);

        if (user.isPresent()) {
            var websiteUser = user.get();
            return User.builder()
                    .username(websiteUser.getEmail())
                    .password(websiteUser.getPassword())
                    .roles(websiteUser.getRoles().stream().map(Role::getRoleName).toArray(String[]::new))
                    .build();
        }else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}