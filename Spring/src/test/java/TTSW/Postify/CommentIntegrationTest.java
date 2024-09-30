package TTSW.Postify;

import TTSW.Postify.dto.CommentDTO;
import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.mapper.WebsiteUserMapper;
import TTSW.Postify.repository.CommentRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.repository.WebsiteUserRepository;
import TTSW.Postify.service.CommentService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@Transactional
class CommentIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private WebsiteUserMapper websiteUserMapper;

    @Test
    void testGetAllCommentsForPost_Success() {
        Page<CommentDTO> comments = commentService.getAllCommentsForPost(1L, PageRequest.of(0, 10));
        assertNotNull(comments);
        assertTrue(comments.getTotalElements() >= 0);
    }

    @Test
    void testGetAllCommentsForPost_postNotFound() {
        assertThrows(EntityNotFoundException.class, () -> commentService.getAllCommentsForPost(999L, PageRequest.of(0, 10)));
    }

    @Test
    @WithMockUser("john@example.com")
    void testCreateComment_Success() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText("New comment");
        commentDTO.setPostId(1L);
        WebsiteUserDTO websiteUserDTO = websiteUserMapper.toDto(websiteUserRepository.findById(1L).get());
        commentDTO.setUser(websiteUserDTO);

        CommentDTO savedComment = commentService.createComment(commentDTO);

        assertNotNull(savedComment);
        assertEquals("New comment", savedComment.getText());
        assertNotNull(savedComment.getId());
    }

    @Test
    @WithMockUser("jane@example.com")
    void testUpdateComment_Success() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setText("Updated comment text");

        CommentDTO updatedComment = commentService.updateComment(commentDTO);

        assertNotNull(updatedComment);
        assertEquals("Updated comment text", updatedComment.getText());
    }

    @Test
    @WithMockUser("johm@example.com")
    void testUpdateComment_notAuthorized() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setText("Not my comment, can I update it?");

        assertThrows(BadCredentialsException.class, () -> commentService.updateComment(commentDTO));
    }

    @Test
    @WithMockUser("jane@example.com")
    void testDeleteComment_Success() {
        commentService.deleteComment(1L);

        assertFalse(commentRepository.findById(1L).isPresent());
    }

    @Test
    @WithMockUser("testadmin@localhost")
    void testDeleteComment_Success_Admin() {
        commentService.deleteComment(1L);

        assertFalse(commentRepository.findById(1L).isPresent());
    }

    @Test
    @WithMockUser("john@example.com")
    void testDeleteComment_notAuthorized() {
        assertThrows(BadCredentialsException.class, () -> commentService.deleteComment(1L));
    }
}
