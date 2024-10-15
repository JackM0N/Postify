package TTSW.Postify.service;

import TTSW.Postify.enums.NotificationType;
import TTSW.Postify.model.Notification;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.PostLike;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.NotificationRepository;
import TTSW.Postify.repository.PostLikeRepository;
import TTSW.Postify.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final WebsiteUserService websiteUserService;
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;

    public Boolean likePost(Long postId) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        PostLike likeExists = postLikeRepository.findByUserIdAndPostId(currentUser.getId(), postId)
                .orElse(null);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post Not Found"));

        if (likeExists != null) {
            postLikeRepository.delete(likeExists);
            Notification existingNotification = notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndPostId(
                    post.getUser().getId(), currentUser.getId(), NotificationType.POST_LIKE, postId
            ).orElse(null);
            if (existingNotification != null && !existingNotification.getIsRead()) {
                notificationRepository.delete(existingNotification);
            }
            return false;
        }

        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(currentUser);
        postLikeRepository.save(postLike);

        Notification notification = new Notification();
        notification.setUser(post.getUser());
        notification.setPost(post);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationType(NotificationType.POST_LIKE);
        notification.setTriggeredBy(currentUser);
        if (notification.getUser() != notification.getTriggeredBy()) {
            notificationRepository.save(notification);
        }

        return true;
    }
}
