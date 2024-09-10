package TTSW.Postify.security;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.mapper.WebsiteUserMapper;
import TTSW.Postify.model.Role;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.RoleRepository;
import TTSW.Postify.repository.WebsiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final WebsiteUserRepository websiteUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final WebsiteUserMapper websiteUserMapper;

    public AuthenticationResponse register(WebsiteUserDTO request) {
        WebsiteUser user = websiteUserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setJoinDate(LocalDate.now());
        user.setProfilePictureUrl(null);

        Role role = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Collections.singletonList(role));
        user = websiteUserRepository.save(user);
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

