package TTSW.Postify.controller;

import TTSW.Postify.dto.CommentDTO;
import TTSW.Postify.service.CommentService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PermitAll
    @GetMapping("/post/{id}")
    public ResponseEntity<Page<CommentDTO>> getComment(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllCommentsForPost(id, pageable));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("")
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(commentService.createComment(commentDTO));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("")
    public ResponseEntity<CommentDTO> editComment(@RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(commentService.updateComment(commentDTO));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
