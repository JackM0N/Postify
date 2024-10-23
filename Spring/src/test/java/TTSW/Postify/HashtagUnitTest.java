package TTSW.Postify;

import TTSW.Postify.dto.HashtagDTO;
import TTSW.Postify.mapper.*;
import TTSW.Postify.model.Hashtag;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.HashtagRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.service.HashtagService;
import TTSW.Postify.service.WebsiteUserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HashtagUnitTest {

    @InjectMocks
    private HashtagService hashtagService;

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private HashtagMapper hashtagMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private WebsiteUserService websiteUserService;

    @Mock
    private WebsiteUser currentUser;

    @Mock
    private Post post;

    @Mock
    private Hashtag hashtag;

    @Mock
    private HashtagDTO hashtagDTO;

    private final Long postId = 1L;
    private final Long hashtagId = 1L;

    @BeforeEach
    void setup() {
        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        hashtagDTO = new HashtagDTO();
        hashtagDTO.setId(hashtagId);
        hashtagDTO.setPostId(postId);
    }

    @Test
    void testUpdateHashtag_Success() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(post.getUser()).thenReturn(currentUser);
        when(hashtagRepository.findById(hashtagId)).thenReturn(Optional.of(hashtag));
        when(hashtag.getPost()).thenReturn(post);
        when(hashtagMapper.toDto(hashtag)).thenReturn(hashtagDTO);

        HashtagDTO updatedHashtag = hashtagService.updateHashtag(postId, hashtagDTO);

        assertEquals(hashtagDTO, updatedHashtag);
        verify(hashtagRepository).save(hashtag);
    }

    @Test
    void testUpdateHashtag_PostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> hashtagService.updateHashtag(postId, hashtagDTO));
        verify(hashtagRepository, never()).save(any(Hashtag.class));
    }

    @Test
    void testUpdateHashtag_HashtagNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(post.getUser()).thenReturn(currentUser);
        when(hashtagRepository.findById(hashtagId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> hashtagService.updateHashtag(postId, hashtagDTO));
        verify(hashtagRepository, never()).save(any(Hashtag.class));
    }

    @Test
    void testUpdateHashtag_UnauthorizedUser() {
        WebsiteUser otherUser = mock(WebsiteUser.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(post.getUser()).thenReturn(otherUser);

        assertThrows(BadCredentialsException.class, () -> hashtagService.updateHashtag(postId, hashtagDTO));
        verify(hashtagRepository, never()).save(any(Hashtag.class));
    }

    @Test
    void testDeleteHashtag_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(post.getUser()).thenReturn(currentUser);
        when(hashtagRepository.findById(hashtagId)).thenReturn(Optional.of(hashtag));
        when(hashtag.getPost()).thenReturn(post);

        hashtagService.deleteHashtag(postId, hashtagId);

        verify(hashtagRepository).delete(hashtag);
    }

    @Test
    void testDeleteHashtag_PostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> hashtagService.deleteHashtag(postId, hashtagId));
        verify(hashtagRepository, never()).delete(any(Hashtag.class));
    }

    @Test
    void testDeleteHashtag_HashtagNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(post.getUser()).thenReturn(currentUser);
        when(hashtagRepository.findById(hashtagId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> hashtagService.deleteHashtag(postId, hashtagId));
        verify(hashtagRepository, never()).delete(any(Hashtag.class));
    }

    @Test
    void testDeleteHashtag_UnauthorizedUser() {
        WebsiteUser otherUser = mock(WebsiteUser.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(post.getUser()).thenReturn(otherUser);

        assertThrows(BadCredentialsException.class, () -> hashtagService.deleteHashtag(postId, hashtagId));
        verify(hashtagRepository, never()).delete(any(Hashtag.class));
    }
}

