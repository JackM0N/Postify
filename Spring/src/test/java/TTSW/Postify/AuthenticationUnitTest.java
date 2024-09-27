package TTSW.Postify;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.mapper.WebsiteUserMapper;
import TTSW.Postify.mapper.WebsiteUserMapperImpl;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.WebsiteUserRepository;
import TTSW.Postify.security.AuthenticationResponse;
import TTSW.Postify.security.AuthenticationService;
import TTSW.Postify.security.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationUnitTest {

    @Mock
    private WebsiteUserRepository websiteUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Spy
    private WebsiteUserMapper websiteUserMapper = new WebsiteUserMapperImpl();

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setEmail("test@example.com");
        request.setPassword("password");

        WebsiteUser user = new WebsiteUser();
        user.setEmail(request.getEmail());

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(websiteUserRepository.save(any(WebsiteUser.class))).thenReturn(user);
        when(jwtService.generateToken(any(WebsiteUser.class))).thenReturn("mockToken");

        AuthenticationResponse response = authenticationService.register(request);

        assertEquals("mockToken", response.token());
        verify(websiteUserRepository).save(any(WebsiteUser.class));
    }

    @Test
    void testAuthenticate_Success() {
        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setEmail("test@example.com");
        request.setPassword("password");

        WebsiteUser user = new WebsiteUser();
        user.setEmail(request.getEmail());

        when(websiteUserRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mockToken");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertEquals("mockToken", response.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testAuthenticate_NoSuchUser() {
        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(websiteUserRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                authenticationService.authenticate(request));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        doThrow(BadCredentialsException.class).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () ->
                authenticationService.authenticate(request));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}