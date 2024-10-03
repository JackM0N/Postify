package TTSW.Postify;

import TTSW.Postify.model.Comment;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.CommentLikeRepository;
import TTSW.Postify.repository.CommentRepository;
import TTSW.Postify.repository.WebsiteUserRepository;
import TTSW.Postify.service.CommentLikeService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CommentLikeIntegrationTest {

    @Autowired
    private CommentLikeService commentLikeService;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private WebsiteUserService websiteUserService;

    @Test
    @WithMockUser("john@example.com")
    void testLikeComment_Success() {
        Comment comment = commentRepository.findById(1L).orElseThrow();

        Boolean result = commentLikeService.likeComment(comment.getId());

        assertTrue(result);
        assertEquals(1, commentLikeRepository.findByUserIdAndCommentId(
                websiteUserService.getCurrentUser().getId(), comment.getId()
        ).stream().toList().size());
    }

    @Test
    @WithMockUser("john@example.com")
    void testLikeComment_RevertLike() {
        WebsiteUser currentUser = websiteUserRepository.findById(1L).orElseThrow();
        Comment comment = commentRepository.findById(2L).orElseThrow();

        commentLikeService.likeComment(comment.getId());

        Boolean result = commentLikeService.likeComment(comment.getId());

        assertFalse(result);
        assertTrue(commentLikeRepository.findByUserIdAndCommentId(currentUser.getId(), comment.getId()).isEmpty());
    }

    @Test
    @WithMockUser("john@example.com")
    void testLikeComment_CommentNotFound() {
        Long nonExistentCommentId = 999L;

        assertThrows(RuntimeException.class, () -> commentLikeService.likeComment(nonExistentCommentId));
    }
}

