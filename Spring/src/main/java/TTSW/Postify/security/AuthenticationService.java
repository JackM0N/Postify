package TTSW.Postify.security;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.enums.Role;
import TTSW.Postify.mapper.WebsiteUserMapper;
import TTSW.Postify.model.UserRole;
import TTSW.Postify.repository.UserRoleRepository;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.WebsiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final WebsiteUserRepository websiteUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final WebsiteUserMapper websiteUserMapper;
    private final UserRoleRepository userRoleRepository;

    public AuthenticationResponse register(WebsiteUserDTO request) {
        WebsiteUser user = websiteUserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRoleName(Role.USER);

        user.setUserRoles(Collections.singletonList(userRole));
        user = websiteUserRepository.save(user);
        userRoleRepository.save(userRole);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(WebsiteUserDTO request) {
        WebsiteUser user;
        String email = request.getEmail();

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                email,
                request.getPassword()
            )
        );

        user = websiteUserRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }
}
