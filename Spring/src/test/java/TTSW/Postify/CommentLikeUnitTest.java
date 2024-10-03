package TTSW.Postify;

import TTSW.Postify.model.Comment;
import TTSW.Postify.model.CommentLike;
import TTSW.Postify.model.Notification;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.CommentLikeRepository;
import TTSW.Postify.repository.CommentRepository;
import TTSW.Postify.repository.NotificationRepository;
import TTSW.Postify.service.CommentLikeService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CommentLikeUnitTest {

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private WebsiteUserService websiteUserService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private CommentLikeService commentLikeService;

    private WebsiteUser currentUser;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currentUser = new WebsiteUser();
        currentUser.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setUser(currentUser);
    }

    @Test
    void testLikeComment_Success() {
        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentLikeRepository.findByUserIdAndCommentId(currentUser.getId(), comment.getId()))
                .thenReturn(Optional.empty());
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        Boolean result = commentLikeService.likeComment(comment.getId());

        assertTrue(result);
        verify(commentLikeRepository).save(any(CommentLike.class));
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testLikeComment_AlreadyLiked() {
        CommentLike existingLike = new CommentLike();
        existingLike.setUser(currentUser);
        existingLike.setComment(comment);

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentLikeRepository.findByUserIdAndCommentId(currentUser.getId(), comment.getId()))
                .thenReturn(Optional.of(existingLike));

        Boolean result = commentLikeService.likeComment(comment.getId());

        assertFalse(result);
        verify(commentLikeRepository).delete(existingLike);
        verify(commentLikeRepository, never()).save(any(CommentLike.class));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void testLikeComment_CommentNotFound() {
        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentLikeRepository.findByUserIdAndCommentId(currentUser.getId(), comment.getId()))
                .thenReturn(Optional.empty());
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentLikeService.likeComment(comment.getId()));
        verify(commentLikeRepository, never()).save(any(CommentLike.class));
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}
