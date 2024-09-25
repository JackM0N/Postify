package TTSW.Postify;

import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.interfaces.HasAuthor;
import TTSW.Postify.mapper.PostMapper;
import TTSW.Postify.model.Medium;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.MediumRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.service.AuthorizationService;
import TTSW.Postify.service.MediumService;
import TTSW.Postify.service.WebsiteUserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.BadCredentialsException;
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

    @Mock
    private AuthorizationService authorizationService;

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
    void testAddMediumAtIndex_NotAuthor() {
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
        WebsiteUser notAuthor = new WebsiteUser();
        notAuthor.setUsername("notAuthor");
        when(websiteUserService.getCurrentUser()).thenReturn(notAuthor);
        when(file.getOriginalFilename()).thenReturn("media.jpg");
        when(postMapper.toDto(post)).thenReturn(new PostDTO());

        assertThrows(BadCredentialsException.class, () -> mediumService.addMediumAtIndex(0, mediumDTO));
    }

    @Test
    void testAddMediumAtIndex_NoSuchPost() {
        Post post = new Post();
        post.setId(1L);
        post.setMedia(new ArrayList<>());
        WebsiteUser user = new WebsiteUser();
        post.setUser(user);
        MultipartFile file = mock(MultipartFile.class);
        MediumDTO mediumDTO = new MediumDTO();
        mediumDTO.setPostId(1L);
        mediumDTO.setFile(file);

        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        when(websiteUserService.getCurrentUser()).thenReturn(user);
        when(file.getOriginalFilename()).thenReturn("media.jpg");
        when(postMapper.toDto(post)).thenReturn(new PostDTO());
        assertThrows(EntityNotFoundException.class, () -> mediumService.addMediumAtIndex(0, mediumDTO));
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
    void testUpdateMedium_NotAuthor() throws IOException {
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
        WebsiteUser notAuthor = new WebsiteUser();
        notAuthor.setUsername("notAuthor");
        when(websiteUserService.getCurrentUser()).thenReturn(notAuthor);
        when(postMapper.toDto(post)).thenReturn(new PostDTO());
        when(file.getInputStream()).thenReturn(mock(InputStream.class));

        assertThrows(BadCredentialsException.class, () -> mediumService.updateMedium(mediumDTO, 0));
    }

    @Test
    void testUpdateMedium_NoSuchMedium() throws IOException {
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

        assertThrows(IndexOutOfBoundsException.class, () -> mediumService.updateMedium(mediumDTO, 99));
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
        when(authorizationService.canModifyEntity(any(HasAuthor.class))).thenReturn(true);

        // Files.deleteIfExists(Path.of(medium.getMediumUrl()));
        // both needs to be mocked
        // Path.of() gets called first
        // Files.delete() cant work with mocked path
        Path path = mock(Path.class);
        try (MockedStatic<Path> pathMock = Mockito.mockStatic(Path.class);
             MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {

            pathMock.when(() -> Path.of(any(String.class))).thenReturn(path);
            filesMock.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(true);

            mediumService.deleteMedium(mediumDTO, 0);
        }

        verify(postRepository).save(post);
    }

    @Test
    void testDeleteMedium_NotAuthorized() {
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
        when(authorizationService.canModifyEntity(any(HasAuthor.class))).thenReturn(false);

        assertThrows((BadCredentialsException.class), () -> mediumService.deleteMedium(mediumDTO, 0));
    }
}



