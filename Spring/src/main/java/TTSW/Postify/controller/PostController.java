package TTSW.Postify.controller;

import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.filter.PostFilter;
import TTSW.Postify.service.PostLikeService;
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
    private final PostLikeService postLikeService;

    @GetMapping("/id/{postId}")
    public ResponseEntity<PostDTO> findById(@PathVariable Long postId){
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<PostDTO>> getPosts(PostFilter filter, Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(filter, pageable));
    }

    @GetMapping("/followed")
    public ResponseEntity<Page<PostDTO>> getFollowedPosts(PostFilter filter, Pageable pageable) {
        return ResponseEntity.ok(postService.getFollowedPosts(filter, pageable));
    }

    @GetMapping("/my-posts")
    public ResponseEntity<Page<PostDTO>> getMyPosts(PostFilter filter, Pageable pageable) {
        return ResponseEntity.ok(postService.getMyPosts(filter, pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(@ModelAttribute PostDTO postDTO) throws IOException {
        return ResponseEntity.ok(postService.createPost(postDTO));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<PostDTO> editPost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.updatePost(id, postDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<Boolean> likePost(@PathVariable Long id) {
        return ResponseEntity.ok(postLikeService.likePost(id));
    }

}
