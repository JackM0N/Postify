package TTSW.Postify;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.RoleRepository;
import TTSW.Postify.repository.WebsiteUserRepository;
import TTSW.Postify.security.AuthenticationResponse;
import TTSW.Postify.security.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AuthenticationIntegrationTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testRegister_Success() {
        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("newpassword");

        AuthenticationResponse response = authenticationService.register(request);

        WebsiteUser user = websiteUserRepository.findByEmail("newuser@example.com").get();
        assertEquals("newuser@example.com", user.getEmail());
        assertTrue(passwordEncoder.matches("newpassword", user.getPassword()));
        assertEquals(1, user.getRoles().size());
        assertEquals("USER", user.getRoles().get(0).getRoleName());
        assertNotNull(response.token());
    }

    @Test
    void testRegister_MailAlreadyUsed() {
        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setEmail("john@example.com");
        request.setPassword("123456");

        assertThrows(Exception.class, () -> authenticationService.register(request));
    }

    @Test
    void testAuthenticate_Success() {
        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setEmail("john@example.com");
        request.setPassword("123456");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response.token());
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setEmail("john@example.com");
        request.setPassword("iForgor");

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void testAuthenticate_NoSuchUser() {
        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password");

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(request));
    }
}
