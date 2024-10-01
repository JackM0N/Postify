package TTSW.Postify.controller;

import TTSW.Postify.dto.HashtagDTO;
import TTSW.Postify.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hashtag")
public class HashtagController {
    private final HashtagService hashtagService;

    @PutMapping("/edit/{postId}")
    public ResponseEntity<HashtagDTO> updateHashtag(@PathVariable Long postId, @RequestBody HashtagDTO hashtagDTO) {
        return ResponseEntity.ok(hashtagService.updateHashtag(postId, hashtagDTO));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<HashtagDTO> deleteHashtag(@PathVariable Long postId, @RequestParam Long hashtagId) {
        hashtagService.deleteHashtag(postId, hashtagId);
        return ResponseEntity.ok().build();
    }
}
