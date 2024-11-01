package TTSW.Postify.service;

import TTSW.Postify.enums.NotificationType;
import TTSW.Postify.model.Comment;
import TTSW.Postify.model.CommentLike;
import TTSW.Postify.model.Notification;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.CommentLikeRepository;
import TTSW.Postify.repository.CommentRepository;
import TTSW.Postify.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final WebsiteUserService websiteUserService;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;

    public Boolean likeComment(Long commentId) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        CommentLike likeExists = commentLikeRepository.findByUserIdAndCommentId(currentUser.getId(), commentId)
                .orElse(null);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (likeExists != null){
            commentLikeRepository.delete(likeExists);
            Notification existingNotification = notificationRepository.findByUserIdAndTriggeredByIdAndNotificationTypeAndCommentId(
                    comment.getUser().getId(), currentUser.getId(), NotificationType.COMMENT_LIKE, commentId
            ).orElse(null);

            if (existingNotification != null && !existingNotification.getIsRead()) {
                notificationRepository.delete(existingNotification);
            }
            return false;
        }
        CommentLike like = new CommentLike();
        like.setComment(comment);
        like.setUser(currentUser);
        commentLikeRepository.save(like);

        Notification notification = new Notification();
        notification.setUser(comment.getUser());
        notification.setComment(comment);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationType(NotificationType.COMMENT_LIKE);
        notification.setTriggeredBy(currentUser);
        if (notification.getUser() != notification.getTriggeredBy()) {
            notificationRepository.save(notification);
        }

        return true;
    }
}
