package TTSW.Postify;

import TTSW.Postify.dto.HashtagDTO;
import TTSW.Postify.model.Hashtag;
import TTSW.Postify.model.Post;
import TTSW.Postify.repository.*;
import TTSW.Postify.service.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@WithMockUser("john@example.com")
public class HashtagIntegrationTest {

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    private Post post;
    private Hashtag hashtag;

    @BeforeEach
    public void setUp() {
        post = postRepository.findById(1L).orElseThrow();
        hashtag = new Hashtag();
        hashtag.setHashtag("hashtag");
        hashtag.setPost(post);
        hashtag = hashtagRepository.save(hashtag);
    }

    // Hashtag creation is tested inside Post, as it is impossible to create hashtag without post, or add it later

    @Test
    void testUpdateHashtag_Success() {
        HashtagDTO updatedHashtagDTO = new HashtagDTO();
        updatedHashtagDTO.setId(hashtag.getId());
        updatedHashtagDTO.setHashtag("UpdatedHashtag");

        HashtagDTO result = hashtagService.updateHashtag(post.getId(), updatedHashtagDTO);

        assertNotNull(result);
        assertEquals("UpdatedHashtag", result.getHashtag());
        assertEquals(hashtag.getId(), result.getId());
    }

    @Test
    @WithMockUser("jane@example.com")
    void testUpdateHashtag_UnauthorizedUser() {
        HashtagDTO updatedHashtagDTO = new HashtagDTO();
        updatedHashtagDTO.setId(hashtag.getId());
        updatedHashtagDTO.setHashtag("UpdatedHashtag");

        assertThrows(BadCredentialsException.class, () -> hashtagService.updateHashtag(post.getId(), updatedHashtagDTO));
    }

    @Test
    void testUpdateHashtag_HashtagNotFound() {
        HashtagDTO updatedHashtagDTO = new HashtagDTO();
        updatedHashtagDTO.setId(999L);
        updatedHashtagDTO.setHashtag("UpdatedHashtag");

        assertThrows(EntityNotFoundException.class, () -> hashtagService.updateHashtag(post.getId(), updatedHashtagDTO));
    }

    @Test
    void testDeleteHashtag_Success() {
        hashtagService.deleteHashtag(post.getId(), hashtag.getId());

        assertTrue(hashtagRepository.findById(hashtag.getId()).isEmpty());
    }

    @Test
    @WithMockUser("jane@example.com")
    void testDeleteHashtag_UnauthorizedUser() {
        assertThrows(BadCredentialsException.class, () -> hashtagService.deleteHashtag(post.getId(), hashtag.getId()));
    }

    @Test
    void testDeleteHashtag_HashtagNotFound() {
        assertThrows(EntityNotFoundException.class, () -> hashtagService.deleteHashtag(post.getId(), 999L));
    }
}

