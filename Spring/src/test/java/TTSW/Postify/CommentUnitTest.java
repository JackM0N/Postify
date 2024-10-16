package TTSW.Postify;

import TTSW.Postify.dto.CommentDTO;
import TTSW.Postify.mapper.*;
import TTSW.Postify.model.Comment;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.CommentRepository;
import TTSW.Postify.repository.NotificationRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.service.AuthorizationService;
import TTSW.Postify.service.CommentService;
import TTSW.Postify.service.WebsiteUserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommentUnitTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private WebsiteUserService websiteUserService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Spy
    private CommentMapper commentMapper = new CommentMapperImpl();

    @Spy
    private SimplifiedWebsiteUserMapper simplifiedWebsiteUserMapper = new SimplifiedWebsiteUserMapperImpl();

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentDTO commentDTO;
    private WebsiteUser user;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new WebsiteUser();
        user.setId(1L);

        post = new Post();
        post.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);
        comment.setPost(post);
        comment.setText("Sample comment");

        commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setText("Sample comment DTO");

        // websiteUserMapper instance inside CommentMapper
        try {
            Field simplifiedWebsiteUserMapperField = CommentMapperImpl.class.getDeclaredField("simplifiedWebsiteUserMapper");
            simplifiedWebsiteUserMapperField.setAccessible(true);
            simplifiedWebsiteUserMapperField.set(commentMapper, simplifiedWebsiteUserMapper);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("Reflection failed, MapperImpl probably changed methods");
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAllCommentsForPost_Success() {
        Page<Comment> commentPage = new PageImpl<>(Collections.singletonList(comment));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(commentPage);

        Page<CommentDTO> result = commentService.getAllCommentsForPost(1L, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(postRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetAllCommentsForPost_PostNotFound() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.getAllCommentsForPost(1L, Pageable.unpaged()));
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateComment_Success() {
        when(websiteUserService.getCurrentUser()).thenReturn(user);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(notificationRepository.save(any())).thenReturn(null);

        CommentDTO result = commentService.createComment(commentDTO);

        assertNotNull(result);
        assertEquals(commentDTO.getText(), result.getText());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testCreateComment_Success_WithParentComment() {
        Comment parentComment = new Comment();
        parentComment.setId(2L);
        comment.setParentComment(parentComment);
        commentDTO.setParentCommentId(parentComment.getId());
        when(websiteUserService.getCurrentUser()).thenReturn(user);
        when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(notificationRepository.save(any())).thenReturn(null);

        CommentDTO result = commentService.createComment(commentDTO);

        assertNotNull(result);
        assertEquals(commentDTO.getText(), result.getText());
        assertEquals(result.getParentCommentId(), parentComment.getId());
        verify(commentRepository, times(1)).findById(parentComment.getId());
    }

    @Test
    void testUpdateComment_Success() {
        when(websiteUserService.getCurrentUser()).thenReturn(user);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        CommentDTO result = commentService.updateComment(commentDTO);

        assertNotNull(result);
        assertEquals(commentDTO.getText(), result.getText());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testUpdateComment_NotAuthor() {
        WebsiteUser anotherUser = new WebsiteUser();
        anotherUser.setId(2L);

        when(websiteUserService.getCurrentUser()).thenReturn(anotherUser);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        assertThrows(BadCredentialsException.class, () -> commentService.updateComment(commentDTO));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testDeleteComment_Success() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(authorizationService.canModifyEntity(comment)).thenReturn(true);

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void testDeleteComment_NotAuthorized() {
        WebsiteUser anotherUser = new WebsiteUser();
        anotherUser.setId(2L);

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(websiteUserService.getCurrentUser()).thenReturn(anotherUser);

        assertThrows(BadCredentialsException.class, () -> commentService.deleteComment(1L));
        verify(commentRepository, never()).delete(any(Comment.class));
    }
}

