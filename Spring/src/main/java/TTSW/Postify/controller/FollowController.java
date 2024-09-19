package TTSW.Postify.controller;

import TTSW.Postify.dto.FollowDTO;
import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {
    private final FollowService followService;

    @GetMapping("/followed-users")
    private ResponseEntity<Page<WebsiteUserDTO>> getFollowedUsers(Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowed(pageable));
    }

    @GetMapping("/followers")
    private ResponseEntity<Page<WebsiteUserDTO>> getFollowers(Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowers(pageable));
    }

    @PostMapping("/create")
    private ResponseEntity<FollowDTO> createFollow(@RequestBody FollowDTO followDTO) {
        return ResponseEntity.ok(followService.createFollow(followDTO));
    }

    @DeleteMapping("/delete")
    private ResponseEntity<?> deleteFollow(@RequestBody FollowDTO followDTO) {
        followService.deleteFollow(followDTO.getId());
        return ResponseEntity.ok().build();
    }
}
