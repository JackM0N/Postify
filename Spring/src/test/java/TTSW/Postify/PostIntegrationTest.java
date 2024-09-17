package TTSW.Postify;

import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.filter.PostFilter;
import TTSW.Postify.mapper.PostMapper;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.MediumRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.service.PostService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PostIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private MediumRepository mediumRepository;

    @Autowired
    private PostMapper postMapper;

    @Value("${directory.media.posts}")
    private final String mediaDirectory = "../Media/posts/";

    @Test
    @Transactional
    @WithAnonymousUser
    public void testGetPosts_Success() {
        PostFilter postFilter = new PostFilter();
        postFilter.setUserId(1L);
        Pageable pageable = PageRequest.of(0, 10);

        Page<PostDTO> postsPage = postService.getPosts(postFilter, pageable);

        assertNotNull(postsPage);
        assertFalse(postsPage.getContent().isEmpty());
        assertEquals(1L, postsPage.getContent().get(0).getUser().getId());
    }


    @Test
    @Transactional
    @WithMockUser("john@example.com")
    public void testCreatePost_Success() throws IOException {
        WebsiteUser user = websiteUserService.getCurrentUser();
        MediumDTO mediumDTO = new MediumDTO();
        mediumDTO.setMediumType("image");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "test_image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes()
        );
        mediumDTO.setFile(multipartFile);

        PostDTO postDTO = new PostDTO();
        postDTO.setDescription("New post description");
        postDTO.setMedia(Collections.singletonList(mediumDTO));

        PostDTO createdPost = postService.createPost(postDTO);

        assertNotNull(createdPost.getId());
        assertEquals("New post description", createdPost.getDescription());
        assertNotNull(createdPost.getCreatedAt());
        assertEquals(user.getId(), createdPost.getUser().getId());

        List<MediumDTO> media = createdPost.getMedia();
        assertEquals(1, media.size());
        assertEquals(mediumDTO.getMediumType(), media.get(0).getMediumType());

        // cleanup
        Path path = Path.of(mediaDirectory + "/" + createdPost.getId());
        if (Files.exists(path)) {
            List<Path> mediaToDelete = Files.list(path).toList();
            for (Path p : mediaToDelete) {
                Files.delete(p);
            }
            Files.delete(path);
        }
    }

    @Test
    @Transactional
    @WithMockUser("john@example.com")
    public void testUpdatePost_Success() throws AccessDeniedException {
        Post post = postRepository.findById(1L).orElse(null);
        assertNotNull(post);
        post.setDescription("New post description");
        LocalDateTime updateDate = post.getUpdatedAt();

        PostDTO updatedPost = postService.updatePost(post.getId(), postMapper.toDto(post));
        assertNotNull(updatedPost);
        assertEquals("New post description", updatedPost.getDescription());
        assertNotEquals(updateDate, updatedPost.getUpdatedAt());
    }
    
    @Test
    @Transactional
    @WithMockUser("jane@example.com")
    public void testUpdatePost_NotOwner() {
        Post post = postRepository.findById(1L).orElse(null);
        assertNotNull(post);
        post.setDescription("New post description");

        assertThrows(Exception.class, () -> postService.updatePost(post.getId(), postMapper.toDto(post)));
    }

    @Test
    @Transactional
    @WithMockUser("john@example.com")
    public void testDeletePost_Success() throws AccessDeniedException{
        boolean isDeleted = postService.deletePost(1L);

        assertTrue(isDeleted);
        Post deletedPost = postRepository.findById(1L).orElse(null);
        assertNotNull(deletedPost);
        assertNotNull(deletedPost.getDeletedAt());
    }

    @Test
    @Transactional
    @WithMockUser("jane@example.com")
    public void testDeletePost_NotOwner() {
        assertThrows((Exception.class), () -> postService.deletePost(1L));
    }

}
