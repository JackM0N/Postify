package TTSW.Postify;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.model.Follow;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.FollowRepository;
import TTSW.Postify.repository.WebsiteUserRepository;
import TTSW.Postify.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FollowHelper {

    @Autowired
    private FollowService followService;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private FollowRepository followRepository;

    public WebsiteUser ensureJaneIsFollowing(WebsiteUser user) {
        List<WebsiteUserDTO> followers = followService.getFollowers(null, Pageable.unpaged()).stream().toList();
        boolean isJaneHere = followers.stream().anyMatch(f -> f.getId() == 2L);

        WebsiteUser jane = websiteUserRepository.findByUsername("jane_smith").get();
        if (!isJaneHere) {
            Follow follow = new Follow();
            follow.setFollowed(user);
            follow.setFollower(jane);
            follow.setCreatedAt(LocalDateTime.now());
            followRepository.save(follow);
        }
        return jane;
    }
}
