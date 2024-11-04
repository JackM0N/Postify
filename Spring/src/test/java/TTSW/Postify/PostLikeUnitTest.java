package TTSW.Postify;

import TTSW.Postify.model.Notification;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.PostLike;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.NotificationRepository;
import TTSW.Postify.repository.PostLikeRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.service.PostLikeService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PostLikeUnitTest {

    @Mock
    private WebsiteUserService websiteUserService;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private PostLikeService postLikeService;

    private WebsiteUser currentUser;
    private Post post;

    @BeforeEach
    void setUp() {
        currentUser = new WebsiteUser();
        currentUser.setId(1L);
        currentUser.setUsername("john_doe");

        WebsiteUser otherUser = new WebsiteUser();
        otherUser.setId(2L);
        otherUser.setUsername("jane_smith");

        post = new Post();
        post.setId(1L);
        post.setUser(otherUser);
    }

    @Test
    void testLikePost_Success() {
        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(postLikeRepository.findByUserIdAndPostId(currentUser.getId(), post.getId())).thenReturn(Optional.empty());
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        Boolean result = postLikeService.likePost(post.getId());

        verify(postLikeRepository, times(1)).save(any(PostLike.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));

        assertTrue(result);
    }

    @Test
    void testLikePost_RevertLike() {
        PostLike existingPostLike = new PostLike();
        existingPostLike.setId(1L);
        existingPostLike.setUser(currentUser);
        existingPostLike.setPost(post);

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(postLikeRepository.findByUserIdAndPostId(currentUser.getId(), post.getId())).thenReturn(Optional.of(existingPostLike));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        Boolean result = postLikeService.likePost(post.getId());

        verify(postLikeRepository, times(1)).delete(existingPostLike);
        verify(postLikeRepository, times(0)).save(any(PostLike.class));

        assertFalse(result);
    }

    @Test
    void testLikePost_PostNotFound() {
        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(postLikeRepository.findByUserIdAndPostId(currentUser.getId(), 999L)).thenReturn(Optional.empty());
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> postLikeService.likePost(999L));
    }
}

