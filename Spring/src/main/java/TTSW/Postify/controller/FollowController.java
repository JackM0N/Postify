package TTSW.Postify.controller;

import TTSW.Postify.dto.FollowDTO;
import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {
    private final FollowService followService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/followed-users")
    private ResponseEntity<Page<WebsiteUserDTO>> getFollowedUsers(Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowed(pageable));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/followers")
    private ResponseEntity<Page<WebsiteUserDTO>> getFollowers(Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowers(pageable));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("")
    private ResponseEntity<FollowDTO> createFollow(@RequestBody FollowDTO followDTO) {
        return ResponseEntity.ok(followService.createFollow(followDTO));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{username}")
    private ResponseEntity<?> deleteFollow(@PathVariable String username) {
        followService.deleteFollow(username);
        return ResponseEntity.ok().build();
    }
}
