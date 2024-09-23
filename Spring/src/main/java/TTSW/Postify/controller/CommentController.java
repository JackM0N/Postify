package TTSW.Postify.controller;

import TTSW.Postify.dto.CommentDTO;
import TTSW.Postify.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    //TODO: PreAuthorize
    @GetMapping("/post/{id}")
    public ResponseEntity<Page<CommentDTO>> getComment(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllCommentsForPost(id, pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(commentService.createComment(commentDTO));
    }

    @PutMapping("/edit")
    public ResponseEntity<CommentDTO> editComment(@RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(commentService.updateComment(commentDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
