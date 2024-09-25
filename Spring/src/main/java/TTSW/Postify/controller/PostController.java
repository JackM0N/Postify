package TTSW.Postify.controller;

import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.filter.PostFilter;
import TTSW.Postify.service.PostService;
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
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PermitAll
    @GetMapping("")
    public ResponseEntity<Page<PostDTO>> getPosts(PostFilter filter, Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(filter, pageable));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("")
    public ResponseEntity<PostDTO> createPost(@ModelAttribute PostDTO postDTO) throws IOException {
        return ResponseEntity.ok(postService.createPost(postDTO));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> editPost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.updatePost(id, postDTO));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

}
