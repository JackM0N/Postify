package TTSW.Postify;

import TTSW.Postify.dto.FollowDTO;
import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.enums.NotificationType;
import TTSW.Postify.model.Notification;
import TTSW.Postify.repository.NotificationRepository;
import TTSW.Postify.service.FollowService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FollowIntegrationTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    @WithMockUser("john@example.com")
    void testGetFollowers_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<WebsiteUserDTO> result = followService.getFollowers(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(5, result.get().findFirst().get().getId());
    }

    @Test
    @WithMockUser("john@example.com")
    void testGetFollowed_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<WebsiteUserDTO> result = followService.getFollowed(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(2, result.get().findFirst().get().getId());
    }

    @Test
    @WithMockUser("john@example.com")
    void testCreateFollow_Success() {
        WebsiteUserDTO followedUserDTO = new WebsiteUserDTO();
        followedUserDTO.setId(3L);
        FollowDTO followDTO = new FollowDTO();
        followDTO.setFollowed(followedUserDTO);

        FollowDTO result = followService.createFollow(followDTO);

        assertNotNull(result);
        assertEquals(3L, result.getFollowed().getId());

        // check if notification was made
        List<Notification> notifications = notificationRepository.findAll();
        Notification followedNotification = null;
        for (Notification notification : notifications) {
            if (notification.getNotificationType() == NotificationType.FOLLOW
                    && Objects.equals(notification.getUser().getId(), followedUserDTO.getId())
                    && notification.getTriggeredBy().getId().equals(1L)) {
                followedNotification = notification;
                break;
            }
        }
        assertNotNull(followedNotification);
    }

    @Test
    @WithMockUser("john@example.com")
    void testDeleteFollow_Success() {
        String followedUsername = "jane_smith";

        followService.deleteFollow(followedUsername);

        Pageable pageable = PageRequest.of(0, 10);
        Page<WebsiteUserDTO> result = followService.getFollowed(null, pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    @WithMockUser("john@example.com")
    void testDeleteFollow_FollowNotFound() {
        String followedUsername = "non_existent_user";

        assertThrows(RuntimeException.class, () -> followService.deleteFollow(followedUsername));
    }
}
