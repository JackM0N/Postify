package TTSW.Postify.service;

import TTSW.Postify.dto.FollowDTO;
import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.enums.NotificationType;
import TTSW.Postify.mapper.FollowMapper;
import TTSW.Postify.mapper.WebsiteUserMapper;
import TTSW.Postify.model.Follow;
import TTSW.Postify.model.Notification;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.FollowRepository;
import TTSW.Postify.repository.NotificationRepository;
import TTSW.Postify.repository.WebsiteUserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    //TODO: Make method to check if currently viewed user is followed by logged in user
    //TODO: Add filter to followers (user.username, created_at)
    private final FollowRepository followRepository;
    private final WebsiteUserRepository websiteUserRepository;
    private final WebsiteUserService websiteUserService;
    private final WebsiteUserMapper websiteUserMapper;
    private final FollowMapper followMapper;
    private final NotificationRepository notificationRepository;

    public Page<WebsiteUserDTO> getFollowers(Pageable pageable) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        List<Follow> follows = followRepository.findByFollowedId(currentUser.getId());
        if (follows == null || follows.isEmpty()) {
            return Page.empty();
        }
        List<Long> followerIds = follows.stream()
                .map(follow -> follow.getFollower().getId())
                .collect(Collectors.toList());

        return getWebsiteUserDTOS(pageable, followerIds);
    }

    public Page<WebsiteUserDTO> getFollowed(Pageable pageable) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        List<Follow> followed = followRepository.findByFollowerId(currentUser.getId());
        if (followed == null || followed.isEmpty()) {
            return Page.empty();
        }
        List<Long> followedIds = followed.stream()
                .map(follow -> follow.getFollowed().getId())
                .collect(Collectors.toList());

        return getWebsiteUserDTOS(pageable, followedIds);
    }

    public Page<WebsiteUserDTO> getWebsiteUserDTOS(Pageable pageable, List<Long> followerIds) {
        Specification<WebsiteUser> spec = (root, query, builder) -> {
            if (!followerIds.isEmpty()) {
                return root.get("id").in(followerIds);
            } else {
                return builder.conjunction();
            }
        };

        Page<WebsiteUser> followers = websiteUserRepository.findAll(spec, pageable);

        return followers.map(websiteUserMapper::toDto);
    }

    public FollowDTO createFollow(FollowDTO followDTO) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        WebsiteUser followedUser = websiteUserRepository.findById(followDTO.getFollowed().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        followRepository.findByFollowedIdAndFollowerId(followedUser.getId(), currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User is already followed by you"));

        Follow follow = followMapper.toEntity(followDTO);
        follow.setFollower(currentUser);
        follow.setFollowed(followedUser);

        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setTriggeredBy(currentUser);
        notification.setUser(followedUser);
        notification.setNotificationType(NotificationType.FOLLOW);

        notificationRepository.save(notification);
        followRepository.save(follow);

        return followMapper.toDto(follow);
    }

    //TODO: Ask which is better: spec or repo method (from comment/postlike)
    public void deleteFollow(String followedUsername) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        WebsiteUser followedUser = websiteUserRepository.findByUsername(followedUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Follow follow = followRepository.findByFollowedIdAndFollowerId(followedUser.getId(), currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Follow not found"));

        followRepository.deleteById(follow.getId());

        Specification<Notification> specification = getNotificationSpecification(followedUsername, currentUser);

        List<Notification> notificationsToDelete = notificationRepository.findAll(specification);
        if(!notificationsToDelete.isEmpty()) {
            notificationRepository.deleteAll(notificationsToDelete);
        }
    }

    private static Specification<Notification> getNotificationSpecification(String followedUsername, WebsiteUser currentUser) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(builder.equal(root.get("user").get("username"), followedUsername));
            predicates.add(builder.equal(root.get("triggeredBy"), currentUser));
            predicates.add(builder.isNull(root.get("post")));
            predicates.add(builder.isNull(root.get("comment")));
            predicates.add(builder.isFalse(root.get("isRead")));
            predicates.add(builder.equal(root.get("notificationType"), NotificationType.FOLLOW));

            // Combine all predicates with AND
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
