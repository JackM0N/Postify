package TTSW.Postify.controller;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.security.AuthenticationResponse;
import TTSW.Postify.security.AuthenticationService;
import TTSW.Postify.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JWTService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody WebsiteUserDTO request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody WebsiteUserDTO request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam("token") String token) {
        try {
            String refreshedToken = jwtService.refreshToken(token);
            return ResponseEntity.ok(new AuthenticationResponse(refreshedToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token cannot be refreshed, please log in again.");
        }
    }
}
