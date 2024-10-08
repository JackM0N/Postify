package TTSW.Postify;

import TTSW.Postify.enums.NotificationType;
import TTSW.Postify.model.Comment;
import TTSW.Postify.model.Notification;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.*;
import TTSW.Postify.service.CommentLikeService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@WithMockUser("john@example.com")
public class CommentLikeIntegrationTest {

    @Autowired
    private CommentLikeService commentLikeService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FollowHelper followHelper;

    private WebsiteUser john;
    private WebsiteUser jane;
    private Comment comment;

    @BeforeEach
    public void setup() {
        john = websiteUserService.getCurrentUser();
        jane = followHelper.ensureJaneIsFollowing(john);
        comment = commentRepository.findById(1L).orElseThrow();
    }


    @Test
    void testLikeComment_Success() {
        Boolean result = commentLikeService.likeComment(comment.getId());

        assertTrue(result);
        assertTrue(commentLikeRepository.findByUserIdAndCommentId(
                websiteUserService.getCurrentUser().getId(), comment.getId()).isPresent());
        assertTrue(notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndCommentId(
                jane.getId(), john.getId(), NotificationType.COMMENT_LIKE, comment.getId()).isPresent());
    }

    @Test
    void testLikeComment_CommentNotFound() {
        Long nonExistentCommentId = 999L;

        assertThrows(RuntimeException.class, () -> commentLikeService.likeComment(nonExistentCommentId));
    }

    @Test
    void testLikeComment_RevertLike_NoNotification() {
        // john liked comment
        // jane received and possibly deleted notification
        // john unliked comment

        commentLikeService.likeComment(comment.getId());

        Boolean result = commentLikeService.likeComment(comment.getId());

        assertFalse(result);
        assertTrue(commentLikeRepository.findByUserIdAndCommentId(john.getId(), comment.getId()).isEmpty());
    }

    @Test
    void testLikeComment_RevertLike_NotificationUnread() {
        // john liked comment
        // john unliked comment
        // jane should not receive like notification as it is no longer true

        // like
        Boolean result = commentLikeService.likeComment(comment.getId());

        assertTrue(result);
        assertTrue(commentLikeRepository.findByUserIdAndCommentId(
                john.getId(), comment.getId()).isPresent());
        assertTrue(notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndCommentId(
                jane.getId(), john.getId(), NotificationType.COMMENT_LIKE, comment.getId()).isPresent());

        // revert
        result = commentLikeService.likeComment(comment.getId());

        assertFalse(result);
        assertTrue(commentLikeRepository.findByUserIdAndCommentId(john.getId(), comment.getId()).isEmpty());
        assertTrue(notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndCommentId(
                jane.getId(), john.getId(), NotificationType.COMMENT_LIKE, comment.getId()).isEmpty());
    }

    @Test
    void testLikeComment_RevertLike_NotificationRead() {
        // john liked comment
        // jane received notification
        // john unliked comment
        // jane's notification should still be there

        // like
        Boolean result = commentLikeService.likeComment(comment.getId());

        assertTrue(result);
        assertTrue(commentLikeRepository.findByUserIdAndCommentId(
                websiteUserService.getCurrentUser().getId(), comment.getId()).isPresent());

        // read notification
        Notification notification = notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndCommentId(
                jane.getId(), john.getId(), NotificationType.COMMENT_LIKE, comment.getId()).orElseThrow();
        notification.setIsRead(true);
        notificationRepository.save(notification);

        // revert
        result = commentLikeService.likeComment(comment.getId());

        assertFalse(result);
        assertTrue(commentLikeRepository.findByUserIdAndCommentId(john.getId(), comment.getId()).isEmpty());
        assertTrue(notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndCommentId(
                jane.getId(), john.getId(), NotificationType.COMMENT_LIKE, comment.getId()).isPresent());
    }
}

