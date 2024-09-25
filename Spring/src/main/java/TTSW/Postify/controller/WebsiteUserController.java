package TTSW.Postify.controller;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.filter.WebsiteUserFilter;
import TTSW.Postify.service.WebsiteUserService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class WebsiteUserController {
    private final WebsiteUserService websiteUserService;

    @PermitAll
    @GetMapping("/{username}")
    private ResponseEntity<WebsiteUserDTO> getUser(@PathVariable String username) {
        WebsiteUserDTO websiteUserDTO = websiteUserService.getWebsiteUser(username);
        if (websiteUserDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(websiteUserDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    private ResponseEntity<Page<WebsiteUserDTO>> getAllUsers(WebsiteUserFilter websiteUserFilter, Pageable pageable) {
        return ResponseEntity.ok(websiteUserService.getWebsiteUsers(websiteUserFilter, pageable));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/edit-profile")
    private ResponseEntity<WebsiteUserDTO> editProfile(@ModelAttribute WebsiteUserDTO websiteUserDTO) throws IOException {
        return ResponseEntity.ok(websiteUserService.editWebsiteUser(websiteUserDTO));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    private ResponseEntity<Boolean> deleteUser(@PathVariable Long id) throws IOException {
        websiteUserService.deleteWebsiteUser(id);
        return ResponseEntity.ok().build();
    }

}
