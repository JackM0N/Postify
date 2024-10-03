package TTSW.Postify.repository;

import TTSW.Postify.enums.NotificationType;
import TTSW.Postify.model.Notification;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByUserIdAndTriggeredByIdAndNotificationTypeAndPostId(
            Long user_id, Long triggeredBy_id, @NotNull NotificationType notificationType, Long post_id
    );
    Optional<Notification> findByUserIdAndTriggeredByIdAndNotificationTypeAndCommentId(
            Long user_id, Long triggeredBy_id, @NotNull NotificationType notificationType, Long comment_id
    );
}
