package TTSW.Postify;

import TTSW.Postify.enums.NotificationType;
import TTSW.Postify.model.Notification;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.NotificationRepository;
import TTSW.Postify.repository.PostLikeRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.repository.WebsiteUserRepository;
import TTSW.Postify.service.PostLikeService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@WithMockUser("john@example.com")
public class PostLikeIntegrationTest {

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    private WebsiteUser john;
    private WebsiteUser jane;
    private Post post;


    @BeforeEach
    public void setUp() {
        try {
            john = websiteUserService.getCurrentUser();
            jane = websiteUserRepository.findByUsername("jane_smith").get();
        } catch (BadCredentialsException ignored) {
        } // for anonymous tests it can fail
        post = postRepository.findById(2L).get();
        postLikeRepository.deleteAll();
        notificationRepository.deleteAll();
    }

    @Test
    void testLikePost_Success() {
        // john likes jane's post
        // jane receives notification
        Boolean result = postLikeService.likePost(post.getId());

        assertTrue(result);
        assertTrue(postLikeRepository.findByUserIdAndPostId(john.getId(), post.getId()).isPresent());
        assertTrue(notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndPostId(
                jane.getId(), john.getId(), NotificationType.POST_LIKE, post.getId()).isPresent());
    }

    @Test
    void testLikePost_PostNotFound() {
        assertThrows(RuntimeException.class, () -> postLikeService.likePost(999L));
    }

    @Test
    @WithAnonymousUser
    void testLikePost_NotLoggedIn() {
        assertThrows(BadCredentialsException.class, () -> postLikeService.likePost(post.getId()));
    }

    @Test
    void testLikePost_RevertLike_NotificationUnread() {
        // john likes jane's post
        // jane receives notification
        // john unlikes jane's post
        // notification should be deleted

        postLikeService.likePost(post.getId());
        Boolean result = postLikeService.likePost(post.getId());

        assertFalse(result);
        assertFalse(postLikeRepository.findByUserIdAndPostId(john.getId(), post.getId()).isPresent());
        assertFalse(notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndPostId(
                jane.getId(), john.getId(), NotificationType.POST_LIKE, post.getId()).isPresent());
    }

    @Test
    void testLikePost_RevertLike_NotificationRead() {
        // john likes jane's post
        // jane receives notification
        // jane reads notification
        // john unlikes jane's post
        // notification should stay

        postLikeService.likePost(post.getId());
        Notification notification = notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndPostId(
                jane.getId(), john.getId(), NotificationType.POST_LIKE, post.getId()).get();
        notification.setIsRead(true);
        notificationRepository.save(notification);

        Boolean result = postLikeService.likePost(post.getId());

        assertFalse(result);
        assertFalse(postLikeRepository.findByUserIdAndPostId(john.getId(), post.getId()).isPresent());
        assertTrue(notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndPostId(
                jane.getId(), john.getId(), NotificationType.POST_LIKE, post.getId()).isPresent());
    }

    @Test
    void testLikePost_RevertLike_NotificationDeleted() {
        // john likes jane's post
        // jane receives notification
        // jane reads notification and deletes it
        // john unlikes jane's post

        postLikeService.likePost(post.getId());
        Notification notification = notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndPostId(
                jane.getId(), john.getId(), NotificationType.POST_LIKE, post.getId()).get();
        notificationRepository.delete(notification);

        Boolean result = postLikeService.likePost(post.getId());

        assertFalse(result);
        assertFalse(postLikeRepository.findByUserIdAndPostId(john.getId(), post.getId()).isPresent());
        assertFalse(notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndPostId(
                jane.getId(), john.getId(), NotificationType.POST_LIKE, post.getId()).isPresent());
    }
}
