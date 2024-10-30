package TTSW.Postify.controller;

import TTSW.Postify.dto.MediumBase64DTO;
import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.filter.WebsiteUserFilter;
import TTSW.Postify.security.AuthenticationResponse;
import TTSW.Postify.service.MediumService;
import TTSW.Postify.service.WebsiteUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class WebsiteUserController {
    private final WebsiteUserService websiteUserService;
    private final MediumService mediumService;

    @GetMapping("/profile/{username}")
    private ResponseEntity<WebsiteUserDTO> getUser(@PathVariable String username) {
        WebsiteUserDTO websiteUserDTO = websiteUserService.getWebsiteUser(username);
        if (websiteUserDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(websiteUserDTO);
    }

    @GetMapping("/pfp/{userId}")
    private ResponseEntity<MediumBase64DTO> getPfp(@PathVariable Long userId) throws IOException {
        byte[] bytes = websiteUserService.getUserProfilePicture(userId);
        if (bytes == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                new MediumBase64DTO(Base64.getEncoder().encodeToString(bytes), mediumService.getMediaType(bytes))
        );
    }

    @GetMapping("/account")
    private ResponseEntity<WebsiteUserDTO> getMyProfile() {
        return ResponseEntity.ok(websiteUserService.getCurrentUserProfile());
    }

    @GetMapping("/list")
    private ResponseEntity<Page<WebsiteUserDTO>> getAllUsers(WebsiteUserFilter websiteUserFilter, Pageable pageable) {
        return ResponseEntity.ok(websiteUserService.getWebsiteUsers(websiteUserFilter, pageable));
    }

    @PutMapping("/edit-profile")
    private ResponseEntity<AuthenticationResponse> editProfile(@ModelAttribute WebsiteUserDTO websiteUserDTO) throws IOException {
        return ResponseEntity.ok(websiteUserService.editWebsiteUser(websiteUserDTO));
    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<Boolean> deleteUser(@PathVariable Long id) throws IOException {
        websiteUserService.deleteWebsiteUser(id);
        return ResponseEntity.ok().build();
    }

}
