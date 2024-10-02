package TTSW.Postify;

import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.PostLikeRepository;
import TTSW.Postify.service.PostLikeService;
import TTSW.Postify.service.WebsiteUserService;
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
public class PostLikeIntegrationTest {

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private WebsiteUserService websiteUserService;

    @Test
    @WithMockUser("john@example.com")
    void testLikePost_Success() {
        Boolean result = postLikeService.likePost(1L);

        assertTrue(result);
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        assertTrue(postLikeRepository.findByUserIdAndPostId(currentUser.getId(), 1L).isPresent());
    }

    @Test
    @WithMockUser("john@example.com")
    void testLikePost_RevertLike() {
        postLikeService.likePost(1L);
        Boolean result = postLikeService.likePost(1L);

        assertFalse(result);
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        assertFalse(postLikeRepository.findByUserIdAndPostId(currentUser.getId(), 1L).isPresent());
    }

    @Test
    @WithMockUser("jane@example.com")
    void testLikePost_PostNotFound() {
        assertThrows(RuntimeException.class, () -> postLikeService.likePost(999L));
    }

    @Test
    @WithAnonymousUser
    void testLikePost_NotLoggedIn() {
        assertThrows(BadCredentialsException.class, () -> postLikeService.likePost(1L));
    }
}
