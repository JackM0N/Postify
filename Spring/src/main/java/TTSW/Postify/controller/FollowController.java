package TTSW.Postify.controller;

import TTSW.Postify.dto.FollowDTO;
import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {
    private final FollowService followService;

    @GetMapping("/followed-users")
    private ResponseEntity<Page<WebsiteUserDTO>> getFollowedUsers(String searchText, Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowed(searchText, pageable));
    }

    @GetMapping("/followers")
    private ResponseEntity<Page<WebsiteUserDTO>> getFollowers(String searchText, Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowers(searchText, pageable));
    }

    @GetMapping("/is-followed/{userId}")
    private ResponseEntity<Boolean> isFollowed(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.isFollowed(userId));
    }

    @PostMapping("/create")
    private ResponseEntity<FollowDTO> createFollow(@RequestBody FollowDTO followDTO) {
        return ResponseEntity.ok(followService.createFollow(followDTO));
    }

    @DeleteMapping("/delete/{username}")
    private ResponseEntity<?> deleteFollow(@PathVariable String username) {
        followService.deleteFollow(username);
        return ResponseEntity.ok().build();
    }
}
