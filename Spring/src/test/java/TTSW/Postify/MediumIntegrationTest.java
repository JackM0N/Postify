package TTSW.Postify;

import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.dto.SimplifiedWebsiteUserDTO;
import TTSW.Postify.mapper.SimplifiedWebsiteUserMapperImpl;
import TTSW.Postify.model.Post;
import TTSW.Postify.repository.MediumRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.service.MediumService;
import TTSW.Postify.service.PostService;
import TTSW.Postify.service.Utils;
import TTSW.Postify.service.WebsiteUserService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = "spring.cache.type=none")
@Transactional
@WithMockUser("john@example.com")
public class MediumIntegrationTest {

    @Autowired
    private MediumService mediumService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private PostService postService;

    private static Path mediaDirectory;

    @Autowired
    private SimplifiedWebsiteUserMapperImpl simplifiedWebsiteUserMapperImpl;

    private PostDTO postDTO;

    @BeforeEach
    void setup() throws IOException {
        try {
            // read location
            // get access to mediaDirectory field
            Field mediaDirectory;
            mediaDirectory = MediumService.class.getDeclaredField("mediaDirectory");
            mediaDirectory.setAccessible(true);

            // move all file operations to other directory
            String originalPath = mediaDirectory.get(mediumService).toString();
            if (!originalPath.contains("Tests")) {
                originalPath = "../Tests/" + originalPath.substring(3);
            }
            MediumIntegrationTest.mediaDirectory = Paths.get(originalPath);
            mediaDirectory.set(mediumService, originalPath);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Reflection failed, MediumService probably changed fields", e);
        }

        // override test authentication to properly create post with author john
        Authentication originalAuthentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john@example.com", "password",
                        AuthorityUtils.createAuthorityList("USER")));

        // create new post with 2 media attached
        postDTO = new PostDTO();
        SimplifiedWebsiteUserDTO userDTO = simplifiedWebsiteUserMapperImpl.toDto(websiteUserService.getCurrentUser());
        postDTO.setUser(userDTO);
        postDTO.setDescription("Description");

        MultipartFile multipartFile1 = new MockMultipartFile(
                "file1", "file1.png", "image/png", "test data".getBytes());
        MediumDTO mediumDTO1 = new MediumDTO();
        mediumDTO1.setFile(multipartFile1);
        mediumDTO1.setMediumType("image");

        MultipartFile multipartFile2 = new MockMultipartFile(
                "file2", "file2.png", "image/png", "test data".getBytes());
        MediumDTO mediumDTO2 = new MediumDTO();
        mediumDTO2.setFile(multipartFile2);
        mediumDTO2.setMediumType("image");

        List<MediumDTO> mediumList = new ArrayList<>(2);
        mediumList.add(mediumDTO1);
        mediumList.add(mediumDTO2);
        postDTO.setMedia(mediumList);

        postDTO = postService.createPost(postDTO);

        // restore original authentication
        SecurityContextHolder.getContext().setAuthentication(originalAuthentication);
    }

    @AfterAll
    static void cleanup() throws IOException {
        if (!mediaDirectory.toString().contains("Test")) {
            throw new BadRequestException("Tried to clean " + mediaDirectory + ", something went wrong");
        }
        try {
            Files.walk(mediaDirectory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        System.out.println("Deleting: " + path);
                        path.toFile().delete();
                    });
        } catch (NoSuchFileException ignored) {
            System.out.println("No such file or directory found: " + mediaDirectory);
        } catch (IOException e) {
            System.out.println("An error occurred while cleaning up: " + e.getMessage());
        }
    }


    @Test
    @WithAnonymousUser
    void testGetMediaForPost_Success() throws IOException {
        int originalSize = postDTO.getMedia().size();
        List<byte[]> mediaBytes = mediumService.getMediaForPost(postDTO.getId());

        assertEquals(originalSize, mediaBytes.size());
    }

    @Test
    @WithMockUser("john@example.com")
    void testAddMediumAtIndex_Success() throws IOException {
        int originalSize = postDTO.getMedia().size();
        MultipartFile additionalMedium = new MockMultipartFile(
                "newMedium", "newMedium.jpg", "image/jpg", "test data".getBytes());
        MediumDTO additionalMediumDTO = new MediumDTO();
        additionalMediumDTO.setFile(additionalMedium);
        additionalMediumDTO.setMediumType("image");
        additionalMediumDTO.setPostId(postDTO.getId());

        postDTO = mediumService.addMediumAtIndex(1, additionalMediumDTO);

        List<MediumDTO> media = postDTO.getMedia();
        assertEquals(originalSize + 1, media.size());
        // media 1 and 2 was png, inserted was jpg
        assertTrue(media.get(0).getMediumUrl().contains("png")); // medium 1
        assertTrue(media.get(1).getMediumUrl().contains("jpg")); // inserted medium
        assertTrue(media.get(2).getMediumUrl().contains("png")); // medium 2
    }

    @Test
    @WithMockUser("jane@example.com")
    void testAddMediumAtIndex_NotAuthor() {
        MultipartFile additionalMedium = new MockMultipartFile(
                "newMedium", "newMedium.jpg", "image/jpg", "test data".getBytes());
        MediumDTO additionalMediumDTO = new MediumDTO();
        additionalMediumDTO.setFile(additionalMedium);
        additionalMediumDTO.setMediumType("image");
        additionalMediumDTO.setPostId(postDTO.getId());

        assertThrows((BadCredentialsException.class), () -> mediumService.addMediumAtIndex(0, additionalMediumDTO));
    }

    @Test
    @WithMockUser("john@example.com")
    void testUpdateMedium_Success() throws IOException {
        int mediumPosition = 0;
        byte[] file = "updated data".getBytes();
        MultipartFile updatedMedium = new MockMultipartFile(
                "newMedium", "newMedium.gif", "image/gif", file);
        MediumDTO updatedMediumDTO = new MediumDTO();
        updatedMediumDTO.setFile(updatedMedium);
        updatedMediumDTO.setMediumType("image");
        updatedMediumDTO.setPostId(postDTO.getId());

        mediumService.updateMedium(updatedMediumDTO, mediumPosition);

        List<byte[]> media = mediumService.getMediaForPost(postDTO.getId());
        assertArrayEquals(media.get(mediumPosition), file);
        assertTrue(postDTO.getMedia().get(mediumPosition).getMediumUrl().contains("gif"));
    }

    @Test
    @WithAnonymousUser
    void testUpdateMedium_NoUser() {
        MultipartFile updatedMedium = new MockMultipartFile(
                "newMedium", "newMedium.gif", "image/gif", "test data".getBytes());
        MediumDTO updatedMediumDTO = new MediumDTO();
        updatedMediumDTO.setFile(updatedMedium);
        updatedMediumDTO.setMediumType("image");
        updatedMediumDTO.setPostId(postDTO.getId());

        assertThrows((BadCredentialsException.class), () -> mediumService.updateMedium(updatedMediumDTO, 0));
    }

    @Test
    @WithMockUser("jane.example.com")
    void testUpdateMedium_NotAuthor() {
        MultipartFile updatedMedium = new MockMultipartFile(
                "newMedium", "newMedium.gif", "image/gif", "test data".getBytes());
        MediumDTO updatedMediumDTO = new MediumDTO();
        updatedMediumDTO.setFile(updatedMedium);
        updatedMediumDTO.setMediumType("image");
        updatedMediumDTO.setPostId(postDTO.getId());

        assertThrows((BadCredentialsException.class), () -> mediumService.updateMedium(updatedMediumDTO, 0));
    }

    @Test
    @WithMockUser("john@example.com")
    void testDeleteMedium_Success() throws IOException {
        int mediumPosition = 1;
        Post post = postRepository.findById(postDTO.getId()).get();
        int originalSize = post.getMedia().size();
        MediumDTO mediumDTO = postDTO.getMedia().get(mediumPosition);

        mediumService.deleteMedium(mediumDTO, mediumPosition);

        post = postRepository.findById(postDTO.getId()).get();
        assertEquals(originalSize - 1, post.getMedia().size());
    }

//    @Test
//    @WithMockUser("testadmin@localhost")
//    void testDeleteMedium_Success_Admin() throws IOException {
//        WebsiteUser user = websiteUserService.getCurrentUser();
//        Medium medium = new Medium();
//        Post post = postRepository.findById(1L).get();
//        medium.setPost(post);
//        medium.setMediumType("image");
//        String fileName = "post_" + post.getId() + "_media_0.jpg";
//        medium.setMediumUrl(mediaDirectory.resolve(fileName).toString());
//        mediumRepository.save(medium);
//
//        Path testFile = mediaDirectory.resolve(fileName);
//        Files.write(testFile, "test data".getBytes());
//
//        MediumDTO mediumDTO = new MediumDTO();
//        mediumDTO.setPostId(post.getId());
//        mediumDTO.setId(medium.getId());
//
//        mediumService.deleteMedium(mediumDTO, 0);
//
//        Post updatedPost = postRepository.findById(post.getId()).get();
//        assertEquals(0, updatedPost.getMedia().size());
//        // assertFalse(Files.exists(testFile)); should deleteMedia delete file or only unlink it?
//    }
//
//    @Test
//    @WithMockUser("jane@example.com")
//    void testDeleteMedium_NotAuthor() throws IOException {
//        // Prepare existing media
//        Medium medium = new Medium();
//        Post post = postRepository.findById(1L).get();
//        medium.setPost(post);
//        medium.setMediumType("image");
//        String fileName = "post_" + post.getId() + "_media_0.jpg";
//        medium.setMediumUrl(mediaDirectory.resolve(fileName).toString());
//        mediumRepository.save(medium);
//
//        Path testFile = mediaDirectory.resolve(fileName);
//        Files.write(testFile, "test data".getBytes());
//
//        MediumDTO mediumDTO = new MediumDTO();
//        mediumDTO.setPostId(post.getId());
//        mediumDTO.setId(medium.getId());
//
//        assertThrows((BadCredentialsException.class), () -> mediumService.deleteMedium(mediumDTO, 0));
//    }
}