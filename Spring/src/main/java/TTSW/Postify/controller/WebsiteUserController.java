package TTSW.Postify.controller;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.filter.WebsiteUserFilter;
import TTSW.Postify.service.WebsiteUserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class WebsiteUserController {
    private final WebsiteUserService websiteUserService;

    @GetMapping("/{username}")
    private ResponseEntity<WebsiteUserDTO> getUser(@PathVariable String username) {
        WebsiteUserDTO websiteUserDTO = websiteUserService.getWebsiteUser(username);
        if (websiteUserDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(websiteUserDTO);
    }

    @GetMapping("/list")
    private ResponseEntity<Page<WebsiteUserDTO>> getAllUsers(WebsiteUserFilter websiteUserFilter, Pageable pageable) {
        return ResponseEntity.ok(websiteUserService.getWebsiteUsers(websiteUserFilter, pageable));
    }

    @PutMapping("/edit-profile")
    private ResponseEntity<WebsiteUserDTO> editProfile(@ModelAttribute WebsiteUserDTO websiteUserDTO) throws IOException {
        return ResponseEntity.ok(websiteUserService.editWebsiteUser(websiteUserDTO));
    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<Boolean> deleteUser(@PathVariable Long id) throws IOException {
        return ResponseEntity.ok(websiteUserService.deleteWebsiteUser(id));
    }

}
