package TTSW.Postify.controller;

import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.filter.PostFilter;
import TTSW.Postify.service.PostService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @GetMapping("/list")
    public ResponseEntity<Page<PostDTO>> getPosts(PostFilter filter, Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(filter, pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO) throws IOException {
        return ResponseEntity.ok(postService.createPost(postDTO));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<PostDTO> editPost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.updatePost(id, postDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deletePost(@PathVariable Long id){
        return ResponseEntity.ok(postService.deletePost(id));
    }

}
