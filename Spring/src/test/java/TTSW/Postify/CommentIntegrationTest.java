package TTSW.Postify;

import TTSW.Postify.dto.CommentDTO;
import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.enums.NotificationType;
import TTSW.Postify.mapper.CommentMapperImpl;
import TTSW.Postify.mapper.WebsiteUserMapper;
import TTSW.Postify.model.Comment;
import TTSW.Postify.model.Notification;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.CommentRepository;
import TTSW.Postify.repository.NotificationRepository;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@WithMockUser("john@example.com")
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

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private CommentMapperImpl commentMapper;


    @Test
    @WithAnonymousUser
    void testGetAllCommentsForPost_Success() {
        Page<CommentDTO> comments = commentService.getAllCommentsForPost(1L, PageRequest.of(0, 10));
        assertNotNull(comments);
        assertTrue(comments.getTotalElements() >= 0);
    }

    @Test
    @WithAnonymousUser
    void testGetAllCommentsForPost_postNotFound() {
        assertThrows(EntityNotFoundException.class, () -> commentService.getAllCommentsForPost(999L, PageRequest.of(0, 10)));
    }

    @Test
    void testCreateComment_Success() {
        WebsiteUser john = websiteUserService.getCurrentUser();
        WebsiteUser jane = websiteUserRepository.findByUsername("jane_smith").get();
        Post post = postRepository.findById(2L).get();
        assertEquals(post.getUser(), jane);

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText("New comment");
        commentDTO.setPostId(post.getId());
        commentDTO.setUser(websiteUserMapper.toDto(john));

        CommentDTO savedComment = commentService.createComment(commentDTO);

        assertNotNull(savedComment);
        assertEquals("New comment", savedComment.getText());
        assertNotNull(savedComment.getId());

        // notification
        Notification notification = notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndCommentId(
                jane.getId(), john.getId(), NotificationType.COMMENT, savedComment.getId()).get();
        assertNotNull(notification);
    }

    @Test
    void testCreateComment_Success_WithParentComment() {
        WebsiteUser john = websiteUserService.getCurrentUser();
        WebsiteUser jane = websiteUserRepository.findByUsername("jane_smith").get();
        Post post = postRepository.findById(1L).get();
        assertNotNull(post);
        assertEquals(post.getUser(), john);

        Comment janeComment = commentRepository.findById(1L).get();
        assertNotNull(janeComment);
        assertEquals(janeComment.getUser(), jane);

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText("New comment");
        commentDTO.setPostId(post.getId());
        commentDTO.setParentComment(commentMapper.toDto(janeComment));
        commentDTO.setUser(websiteUserMapper.toDto(john));

        CommentDTO savedComment = commentService.createComment(commentDTO);

        assertNotNull(savedComment);
        assertEquals("New comment", savedComment.getText());
        assertNotNull(savedComment.getId());
        assertEquals(savedComment.getParentComment().getId(), janeComment.getId());

        // notification
        Notification notification = notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndCommentId(
                jane.getId(), john.getId(), NotificationType.COMMENT, savedComment.getId()).get();
        assertNotNull(notification);
    }

    @Test
    void testCreateComment_NoSuchPost() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText("New comment");
        commentDTO.setPostId(999L);
        WebsiteUserDTO websiteUserDTO = websiteUserMapper.toDto(websiteUserRepository.findById(1L).get());
        commentDTO.setUser(websiteUserDTO);

        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(commentDTO));
    }

    @Test
    @WithMockUser("jane@example.com")
    void testUpdateComment_Success() {
        Comment comment = commentRepository.findById(1L).get();
        assertNotNull(comment);
        assertEquals(comment.getUser(), websiteUserService.getCurrentUser());
        CommentDTO commentDTO = commentMapper.toDto(comment);
        commentDTO.setText("Updated comment text");

        CommentDTO updatedComment = commentService.updateComment(commentDTO);

        assertNotNull(updatedComment);
        assertEquals("Updated comment text", updatedComment.getText());
    }

    @Test
    void testUpdateComment_notAuthorized() {
        Comment comment = commentRepository.findById(1L).get();
        assertNotNull(comment);
        assertNotEquals(comment.getUser(), websiteUserService.getCurrentUser());
        CommentDTO commentDTO = commentMapper.toDto(comment);
        commentDTO.setText("Not my comment, can I update it?");

        assertThrows(BadCredentialsException.class, () -> commentService.updateComment(commentDTO));
    }

    @Test
    @WithMockUser("jane@example.com")
    void testDeleteComment_Success() {
        Comment comment = commentRepository.findById(1L).get();
        assertNotNull(comment);
        commentService.deleteComment(comment.getId());

        assertFalse(commentRepository.findById(comment.getId()).isPresent());
    }

    @Test
    @WithMockUser("testadmin@localhost")
    void testDeleteComment_Success_Admin() {
        Comment comment = commentRepository.findById(1L).get();
        assertNotNull(comment);
        commentService.deleteComment(comment.getId());

        assertFalse(commentRepository.findById(1L).isPresent());
    }

    @Test
    void testDeleteComment_notAuthorized() {
        Comment comment = commentRepository.findById(1L).get();
        assertNotNull(comment);
        assertNotEquals(comment.getUser(), websiteUserService.getCurrentUser());
        assertThrows(BadCredentialsException.class, () -> commentService.deleteComment(comment.getId()));
    }
}
