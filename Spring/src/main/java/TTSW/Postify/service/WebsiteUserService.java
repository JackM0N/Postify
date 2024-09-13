package TTSW.Postify.service;

import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.WebsiteUserRepository;
import TTSW.Postify.security.IAuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebsiteUserService {
    private final WebsiteUserRepository websiteUserRepository;
    private final IAuthenticationFacade authenticationFacade;

    public WebsiteUser getCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        String username = authentication.getName();
        return websiteUserRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("You are not logged in"));
    }
}
