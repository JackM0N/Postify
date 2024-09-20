package TTSW.Postify;

import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.mapper.PostMapper;
import TTSW.Postify.model.Medium;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.MediumRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.service.MediumService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MediumUnitTest {

    @Mock
    private MediumRepository mediumRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private WebsiteUserService websiteUserService;

    @InjectMocks
    private MediumService mediumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMediaForPost_Success() throws IOException {
        Post post = new Post();
        Medium medium = new Medium();
        medium.setMediumUrl("media.jpg");
        post.setMedia(List.of(medium));

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Mock Static Files
        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            byte[] mockMediaBytes = "mock media content".getBytes();
            filesMock.when(() -> Files.readAllBytes(any(Path.class))).thenReturn(mockMediaBytes);

            List<byte[]> result = mediumService.getMediaForPost(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertArrayEquals(mockMediaBytes, result.get(0));

            filesMock.verify(() -> Files.readAllBytes(any(Path.class)), times(1));
        }
    }

    @Test
    void testAddMediumAtIndex_Success() throws IOException {
        Post post = new Post();
        post.setId(1L);
        post.setMedia(new ArrayList<>());
        WebsiteUser user = new WebsiteUser();
        post.setUser(user);
        MultipartFile file = mock(MultipartFile.class);
        MediumDTO mediumDTO = new MediumDTO();
        mediumDTO.setPostId(1L);
        mediumDTO.setFile(file);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(websiteUserService.getCurrentUser()).thenReturn(user);
        when(file.getOriginalFilename()).thenReturn("media.jpg");
        when(postMapper.toDto(post)).thenReturn(new PostDTO());

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L);

            PostDTO result = mediumService.addMediumAtIndex(0, mediumDTO);

            assertNotNull(result);
            verify(mediumRepository).save(any(Medium.class));
            verify(postRepository).save(post);
        }
    }

    @Test
    void testUpdateMedium_Success() throws IOException {
        Post post = new Post();
        post.setId(1L);
        WebsiteUser user = new WebsiteUser();
        post.setUser(user);

        Medium medium = new Medium();
        medium.setMediumUrl("media.jpg");
        post.setMedia(List.of(medium));

        MultipartFile file = mock(MultipartFile.class);
        MediumDTO mediumDTO = new MediumDTO();
        mediumDTO.setPostId(1L);
        mediumDTO.setFile(file);
        mediumDTO.setId(0L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(websiteUserService.getCurrentUser()).thenReturn(user);
        when(postMapper.toDto(post)).thenReturn(new PostDTO());
        when(file.getInputStream()).thenReturn(mock(InputStream.class));

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(true);
            filesMock.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L);

            PostDTO result = mediumService.updateMedium(mediumDTO, 0);

            assertNotNull(result);
            verify(mediumRepository).save(any(Medium.class));
            verify(postMapper).toDto(post);

            filesMock.verify(() -> Files.deleteIfExists(any(Path.class)), times(1));
            filesMock.verify(() -> Files.copy(any(InputStream.class), any(Path.class)), times(1));
        }
    }

    @Test
    void testDeleteMedium_Success() throws IOException {
        Post post = new Post();
        post.setId(1L);
        WebsiteUser user = new WebsiteUser();
        post.setUser(user);

        Medium medium = new Medium();
        medium.setId(0L);
        post.setMedia(new ArrayList<>(List.of(medium)));

        MediumDTO mediumDTO = new MediumDTO();
        mediumDTO.setPostId(1L);
        mediumDTO.setId(0L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(websiteUserService.getCurrentUser()).thenReturn(user);

        boolean result = mediumService.deleteMedium(mediumDTO,0);

        assertTrue(result);
        verify(postRepository).save(post);
    }

}



