package TTSW.Postify;

import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.filter.PostFilter;
import TTSW.Postify.interfaces.HasAuthor;
import TTSW.Postify.mapper.*;
import TTSW.Postify.model.Medium;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.FollowRepository;
import TTSW.Postify.repository.MediumRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.service.AuthorizationService;
import TTSW.Postify.service.PostService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PostUnitTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Spy
    private MediumMapper mediumMapper = new MediumMapperImpl();

    @Spy
    private PostMapper postMapper = new PostMapperImpl();

    @Spy
    private PostLikeMapper postLikeMapper = new PostLikeMapperImpl();

    @Spy
    private SimplifiedWebsiteUserMapper simplifiedWebsiteUserMapper = new SimplifiedWebsiteUserMapperImpl();

    @Mock
    private MediumRepository mediumRepository;

    @Mock
    private WebsiteUserService websiteUserService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private FollowRepository followRepository;

    @Value("${directory.media.posts}")
    private String mediaDirectory = "../Media/posts/";

    @Mock
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // set Mappers inside PostMapper to actual object
        // using reflection bc @Spy or @InjectMocks fails there
        try {
            Field mediumMapperField = PostMapperImpl.class.getDeclaredField("mediumMapper");
            mediumMapperField.setAccessible(true);
            mediumMapperField.set(postMapper, mediumMapper);

            Field postLikeMapperField = PostMapperImpl.class.getDeclaredField("postLikeMapper");
            postLikeMapperField.setAccessible(true);
            postLikeMapperField.set(postMapper, postLikeMapper);

            Field simplifiedWebsiteUserMapperField = PostMapperImpl.class.getDeclaredField("simplifiedWebsiteUserMapper");
            simplifiedWebsiteUserMapperField.setAccessible(true);
            simplifiedWebsiteUserMapperField.set(postMapper, simplifiedWebsiteUserMapper);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("Reflection failed, MapperImpl probably changed methods");
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetPosts_Success() {
        PostFilter postFilter = new PostFilter();
        postFilter.setUserId(1L);
        Page<Post> postPage = new PageImpl<>(List.of(new Post()));
        when(postRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(postPage);

        Page<PostDTO> result = postService.getPosts(postFilter, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(postRepository).findAll(any(Specification.class), eq(pageable));
        verify(postMapper).toDto(any(Post.class));
    }

    @Test
    void testCreatePost_Success() throws IOException {
        Long postId = 1L;
        PostDTO postDTO = new PostDTO();
        Post post = new Post();
        post.setId(postId);
        MediumDTO mediumDTO = new MediumDTO();
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
        mediumDTO.setFile(file);
        postDTO.setMedia(List.of(mediumDTO));

        WebsiteUser user = new WebsiteUser();
        user.setUsername("testuser");
        user.setId(1L);
        post.setUser(user);
        when(websiteUserService.getCurrentUser()).thenReturn(user);
        when(followRepository.findByFollowedId(any(Long.class))).thenReturn(new ArrayList<>());

        // original save() updates id of saved object, this is simulated here
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            savedPost.setId(postId);
            return savedPost;
        });

        PostDTO result;
        // Mock Files.copy() so it does not save any files
        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.copy(any(), any())).thenReturn(1L);

            result = postService.createPost(postDTO);

            filesMock.verify(() -> Files.copy(any(InputStream.class), any(Path.class)), times(1));
        }
        assertNotNull(result);
        verify(postRepository, times(2)).save(any(Post.class));
        verify(mediumRepository).save(any(Medium.class));
    }

    @Test
    void testCreatePost_NoMedia() {
        PostDTO postDTO = new PostDTO();
        postDTO.setMedia(null);

        assertThrows((RuntimeException.class), () -> postService.createPost(postDTO));
    }

    @Test
    void testUpdatePost_Success() {
        Long postId = 1L;
        Post post = new Post();
        WebsiteUser user = new WebsiteUser();
        post.setId(postId);
        post.setUser(user);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        PostDTO postDTO = new PostDTO();
        when(postRepository.save(any(Post.class))).thenReturn(post);

        when(websiteUserService.getCurrentUser()).thenReturn(user);

        PostDTO result = postService.updatePost(postId, postDTO);

        assertNotNull(result);
        verify(postRepository).findById(postId);
        verify(postRepository).save(any(Post.class));
        verify(postMapper).partialUpdate(any(PostDTO.class), any(Post.class));
    }

    @Test
    void testUpdatePost_NotAuthor() {
        Long postId = 1L;
        Post post = new Post();
        WebsiteUser user = new WebsiteUser();
        post.setId(postId);
        post.setUser(user);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        PostDTO postDTO = new PostDTO();
        when(postRepository.save(any(Post.class))).thenReturn(post);

        WebsiteUser notAuthor = new WebsiteUser();
        notAuthor.setUsername("notAuthor");
        when(websiteUserService.getCurrentUser()).thenReturn(notAuthor);

        assertThrows(BadCredentialsException.class, () -> postService.updatePost(postId, postDTO));
    }

    @Test
    void testUpdatePost_NoSuchPost() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        PostDTO postDTO = new PostDTO();

        assertThrows((RuntimeException.class), () -> postService.updatePost(postId, postDTO));
    }

    @Test
    void testDeletePost_Success() {
        Long postId = 1L;
        Post post = new Post();
        WebsiteUser user = new WebsiteUser();
        post.setId(postId);
        post.setUser(user);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(authorizationService.canModifyEntity(any(HasAuthor.class))).thenReturn(true);

        postService.deletePost(postId);

        assertNotNull(post.getDeletedAt());
        verify(postRepository).save(post);
    }

    @Test
    void testDeletePost_NotAuthorized() {
        Long postId = 1L;
        Post post = new Post();
        WebsiteUser user = new WebsiteUser();
        post.setId(postId);
        post.setUser(user);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(authorizationService.canModifyEntity(any(Post.class))).thenReturn(false);

        assertThrows((BadCredentialsException.class), () -> postService.deletePost(postId));
    }

    @Test
    void testDeletePost_NoSuchPost() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows((RuntimeException.class), () -> postService.deletePost(postId));
    }
}
