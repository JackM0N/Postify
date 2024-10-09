package TTSW.Postify;

import TTSW.Postify.dto.FollowDTO;
import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.mapper.FollowMapper;
import TTSW.Postify.mapper.FollowMapperImpl;
import TTSW.Postify.mapper.WebsiteUserMapper;
import TTSW.Postify.mapper.WebsiteUserMapperImpl;
import TTSW.Postify.model.Follow;
import TTSW.Postify.model.Notification;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.FollowRepository;
import TTSW.Postify.repository.NotificationRepository;
import TTSW.Postify.repository.WebsiteUserRepository;
import TTSW.Postify.service.FollowService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FollowUnitTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private WebsiteUserRepository websiteUserRepository;

    @Mock
    private WebsiteUserService websiteUserService;

    @Spy
    private WebsiteUserMapper websiteUserMapper = new WebsiteUserMapperImpl();

    @Spy
    private FollowMapper followMapper = new FollowMapperImpl();

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private FollowService followService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFollowers_Success() {
        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);
        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);

        Follow follow = new Follow();
        follow.setFollower(currentUser);
        follow.setFollowed(new WebsiteUser());
        List<Follow> follows = Collections.singletonList(follow);
        Page<Follow> page = new PageImpl<>(follows);
        when(followRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10);
        Page<WebsiteUserDTO> mockPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(websiteUserRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

        Page<WebsiteUserDTO> result = followService.getFollowers(null, pageable);

        assertNotNull(result);
        verify(websiteUserRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testGetFollowed_Success() {
        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);
        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);

        Follow follow = new Follow();
        follow.setFollower(currentUser);
        follow.setFollowed(new WebsiteUser());
        List<Follow> followed = Collections.singletonList(follow);
        Page<Follow> page = new PageImpl<>(followed);
        when(followRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10);
        Page<WebsiteUserDTO> mockPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(websiteUserRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

        Page<WebsiteUserDTO> result = followService.getFollowed(null, pageable);

        assertNotNull(result);
        verify(websiteUserRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testCreateFollow_Success() {
        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);
        WebsiteUser followedUser = new WebsiteUser();
        followedUser.setId(2L);
        FollowDTO followDTO = new FollowDTO();
        followDTO.setFollowed(websiteUserMapper.toDto(followedUser));

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(websiteUserRepository.findById(2L)).thenReturn(Optional.of(followedUser));
        when(followRepository.findByFollowedIdAndFollowerId(followedUser.getId(), currentUser.getId()))
                .thenReturn(Optional.empty());

        FollowDTO result = followService.createFollow(followDTO);

        assertNotNull(result);
        verify(followRepository, times(1)).save(any(Follow.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testCreateFollow_AlreadyFollowed() {
        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);
        WebsiteUser followedUser = new WebsiteUser();
        followedUser.setId(2L);
        FollowDTO followDTO = new FollowDTO();
        followDTO.setFollowed(websiteUserMapper.toDto(followedUser));

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(websiteUserRepository.findById(2L)).thenReturn(Optional.of(followedUser));
        when(followRepository.findByFollowedIdAndFollowerId(followedUser.getId(), currentUser.getId()))
                .thenReturn(Optional.empty());

        followService.createFollow(followDTO);

        // repeated action throws error
        Follow follow = new Follow();
        follow.setFollowed(followedUser);
        follow.setFollower(currentUser);

        when(followRepository.findByFollowedIdAndFollowerId(followedUser.getId(), currentUser.getId()))
                .thenReturn(Optional.of(follow));

        assertThrows(RuntimeException.class, () -> followService.createFollow(followDTO));
    }

    @Test
    void testCreateFollow_UserNotFound() {
        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);

        FollowDTO followDTO = new FollowDTO();
        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(websiteUserRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> followService.createFollow(followDTO));
        verify(followRepository, times(0)).save(any());
    }

    @Test
    void testDeleteFollow_Success() {
        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);
        WebsiteUser followedUser = new WebsiteUser();
        followedUser.setId(2L);
        followedUser.setUsername("jane_smith");

        Follow follow = new Follow();
        follow.setId(1L);
        follow.setFollower(currentUser);
        follow.setFollowed(followedUser);

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(websiteUserRepository.findByUsername("jane_smith")).thenReturn(Optional.of(followedUser));
        when(followRepository.findByFollowedIdAndFollowerId(2L, 1L)).thenReturn(Optional.of(follow));

        followService.deleteFollow("jane_smith");

        verify(followRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteFollow_FollowNotFound() {
        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);
        WebsiteUser followedUser = new WebsiteUser();
        followedUser.setId(2L);
        followedUser.setUsername("jane_smith");

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(websiteUserRepository.findByUsername("jane_smith")).thenReturn(Optional.of(followedUser));
        when(followRepository.findByFollowedIdAndFollowerId(2L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> followService.deleteFollow("jane_smith"));
        verify(followRepository, times(0)).deleteById(anyLong());
    }
}

