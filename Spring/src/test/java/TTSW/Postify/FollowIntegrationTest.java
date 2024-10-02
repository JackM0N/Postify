package TTSW.Postify;

import TTSW.Postify.dto.FollowDTO;
import TTSW.Postify.dto.WebsiteUserDTO;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FollowIntegrationTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private WebsiteUserService websiteUserService;

    @Test
    @WithMockUser("john@example.com")
    void testGetFollowers_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<WebsiteUserDTO> result = followService.getFollowers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(5, result.get().findFirst().get().getId());
    }

    @Test
    @WithMockUser("john@example.com")
    void testGetFollowed_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<WebsiteUserDTO> result = followService.getFollowed(pageable);

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
    }

    @Test
    @WithMockUser("john@example.com")
    void testDeleteFollow_Success() {
        String followedUsername = "jane_smith";

        followService.deleteFollow(followedUsername);

        Pageable pageable = PageRequest.of(0, 10);
        Page<WebsiteUserDTO> result = followService.getFollowed(pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    @WithMockUser("john@example.com")
    void testDeleteFollow_FollowNotFound() {
        String followedUsername = "non_existent_user";

        assertThrows(RuntimeException.class, () -> followService.deleteFollow(followedUsername));
    }
}
