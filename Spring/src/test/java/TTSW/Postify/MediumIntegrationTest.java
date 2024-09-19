package TTSW.Postify;

import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.model.Medium;
import TTSW.Postify.model.Post;
import TTSW.Postify.repository.MediumRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.service.MediumService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MediumIntegrationTest {

    @Autowired
    private MediumService mediumService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MediumRepository mediumRepository;

    @Autowired
    private WebsiteUserService websiteUserService;

    private static Path tempDirectory;

    @BeforeEach
    void setup() {
        try {
            // get access to mediaDirectory field
            Field mediaDirectory;
            mediaDirectory = MediumService.class.getDeclaredField("mediaDirectory");
            mediaDirectory.setAccessible(true);

            // move all file operations to other directory
            String originalPath = mediaDirectory.get(mediumService).toString();
            if (!originalPath.contains("Tests")) {
                originalPath = "../Tests/" + originalPath.substring(3);
            }
            tempDirectory = Paths.get(originalPath);
            mediaDirectory.set(mediumService, originalPath);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Reflection failed, MediumService probably changed fields", e);
        }
    }

    @AfterAll
    static void cleanup() throws IOException {
        Files.walk(tempDirectory)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    @WithAnonymousUser
    void testGetMediaForPost_Success() throws IOException {
        Medium medium = new Medium();
        Post post = postRepository.findById(1L).get();
        medium.setPost(post);
        medium.setMediumType("image");
        String fileName = "test_image.jpg";
        medium.setMediumUrl(tempDirectory.resolve(fileName).toString());
        mediumRepository.save(medium);

        Path testFile = tempDirectory.resolve(fileName);
        Files.createDirectories(testFile.getParent());
        Files.write(testFile, "test data".getBytes());

        List<byte[]> mediaBytes = mediumService.getMediaForPost(post.getId());

        assertEquals(1, mediaBytes.size());
        assertArrayEquals("test data".getBytes(), mediaBytes.get(0));
    }

    @Test
    @WithMockUser("john@example.com")
    void testAddMediumAtIndex_Success() throws IOException {
        Long postId = 1L;
        MultipartFile multipartFile = new MockMultipartFile(
                "file", "test_image.jpg", "image/jpeg", "test data".getBytes());

        MediumDTO mediumDTO = new MediumDTO();
        mediumDTO.setPostId(postId);
        mediumDTO.setFile(multipartFile);
        mediumDTO.setMediumType("image");

        // this method assumes folder already exists since you cant create post without medium
        Path postDirectory = Path.of(tempDirectory.toString() + "/" + postId);
        if (!Files.exists(postDirectory)) Files.createDirectories(postDirectory);

        mediumService.addMediumAtIndex(0, mediumDTO);

        Post updatedPost = postRepository.findById(postId).get();
        assertEquals(1, updatedPost.getMedia().size());

        String expectedFilePath = tempDirectory.resolve(postId + "/post_" + postId + "_media_0.jpg").toString();
        assertTrue(Files.exists(Paths.get(expectedFilePath)));
    }

    @Test
    @WithMockUser("john@example.com")
    void testUpdateMedium_Success() throws IOException {
        Medium medium = new Medium();
        Post post = postRepository.findById(1L).get();
        medium.setPost(post);
        medium.setMediumType("image");
        String fileName = "post_" + post.getId() + "_media_0.jpg";
        medium.setMediumUrl(tempDirectory.resolve(fileName).toString());
        mediumRepository.save(medium);

        Path testFile = tempDirectory.resolve(fileName);
        Files.createDirectories(testFile.getParent());
        Files.write(testFile, "old data".getBytes());

        MultipartFile newMultipartFile = new MockMultipartFile(
                "file", "updated_image.jpg", "image/jpeg", "updated data".getBytes());
        MediumDTO mediumDTO = new MediumDTO();
        mediumDTO.setPostId(post.getId());
        mediumDTO.setId(medium.getId());
        mediumDTO.setFile(newMultipartFile);
        mediumDTO.setMediumType("image");

        mediumService.updateMedium(mediumDTO);

        Post updatedPost = postRepository.findById(post.getId()).get();
        assertEquals(1, updatedPost.getMedia().size());
        assertArrayEquals("updated data".getBytes(), Files.readAllBytes(testFile));
    }

    @Test
    @WithAnonymousUser
    void testUpdateMedium_NoUser() throws IOException {
        Medium medium = new Medium();
        Post post = postRepository.findById(1L).get();
        medium.setPost(post);
        medium.setMediumType("image");
        String fileName = "post_" + post.getId() + "_media_0.jpg";
        medium.setMediumUrl(tempDirectory.resolve(fileName).toString());
        mediumRepository.save(medium);

        Path testFile = tempDirectory.resolve(fileName);
        Files.createDirectories(testFile.getParent());
        if (!Files.exists(testFile)){
            Files.write(testFile, "old data".getBytes());
        }

        MultipartFile newMultipartFile = new MockMultipartFile(
                "file", "updated_image.jpg", "image/jpeg", "updated data".getBytes());
        MediumDTO mediumDTO = new MediumDTO();
        mediumDTO.setPostId(post.getId());
        mediumDTO.setId(medium.getId());
        mediumDTO.setFile(newMultipartFile);
        mediumDTO.setMediumType("image");

        assertThrows((BadCredentialsException.class), () -> mediumService.updateMedium(mediumDTO));
    }

    @Test
    @WithMockUser("jane.example.com")
    void testUpdateMedium_NotAuthor() throws IOException {
        Medium medium = new Medium();
        Post post = postRepository.findById(1L).get();
        medium.setPost(post);
        medium.setMediumType("image");
        String fileName = "post_" + post.getId() + "_media_0.jpg";
        medium.setMediumUrl(tempDirectory.resolve(fileName).toString());
        mediumRepository.save(medium);

        Path testFile = tempDirectory.resolve(fileName);
        Files.createDirectories(testFile.getParent());
        if (!Files.exists(testFile)){
            Files.write(testFile, "old data".getBytes());
        }

        MultipartFile newMultipartFile = new MockMultipartFile(
                "file", "updated_image.jpg", "image/jpeg", "updated data".getBytes());
        MediumDTO mediumDTO = new MediumDTO();
        mediumDTO.setPostId(post.getId());
        mediumDTO.setId(medium.getId());
        mediumDTO.setFile(newMultipartFile);
        mediumDTO.setMediumType("image");

        assertThrows((BadCredentialsException.class), () -> mediumService.updateMedium(mediumDTO));
    }

    @Test
    @WithMockUser("john@example.com")
    void testDeleteMedium_Success() throws IOException {
        // Prepare existing media
        Medium medium = new Medium();
        Post post = postRepository.findById(1L).get();
        medium.setPost(post);
        medium.setMediumType("image");
        String fileName = "post_" + post.getId() + "_media_0.jpg";
        medium.setMediumUrl(tempDirectory.resolve(fileName).toString());
        mediumRepository.save(medium);

        Path testFile = tempDirectory.resolve(fileName);
        Files.write(testFile, "test data".getBytes());


        MediumDTO mediumDTO = new MediumDTO();
        mediumDTO.setPostId(post.getId());
        mediumDTO.setId(medium.getId());

        boolean result = mediumService.deleteMedium(mediumDTO);

        assertTrue(result);
        Post updatedPost = postRepository.findById(post.getId()).get();
        assertEquals(0, updatedPost.getMedia().size());
        assertFalse(Files.exists(testFile));
    }
}