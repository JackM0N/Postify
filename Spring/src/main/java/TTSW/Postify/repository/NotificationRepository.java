package TTSW.Postify.repository;

import TTSW.Postify.enums.NotificationType;
import TTSW.Postify.model.Notification;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    Optional<Notification> findByUserIdAndTriggeredByIdAndNotificationTypeAndPostId(
            Long user_id, Long triggeredBy_id, @NotNull NotificationType notificationType, Long post_id
    );
    Optional<Notification> findByUserIdAndTriggeredByIdAndNotificationTypeAndCommentId(
            Long user_id, Long triggeredBy_id, @NotNull NotificationType notificationType, Long comment_id
    );

    List<Notification> findByUserId(Long userId);
}
