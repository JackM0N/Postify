package TTSW.Postify.service;

import TTSW.Postify.dto.NotificationDTO;
import TTSW.Postify.mapper.NotificationMapper;
import TTSW.Postify.model.Notification;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final WebsiteUserService websiteUserService;
    private final NotificationMapper notificationMapper;

    public Page<NotificationDTO> getNotifications(Pageable pageable) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        Specification<Notification> specification = (root, query, builder) ->
                builder.equal(root.get("user"), currentUser);
        Page<Notification> notifications = notificationRepository.findAll(specification, pageable);
        notifications.forEach(notification -> notification.setIsRead(true));
        return notifications.map(notificationMapper::toDto);
    }

    public Page<NotificationDTO> getUnreadNotifications(Pageable pageable) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        Specification<Notification> specification = (root, query, builder) ->
                builder.equal(root.get("user"), currentUser);
        specification = specification.and((root, query, builder) -> builder.equal(root.get("isRead"), false));
        Page<Notification> notifications = notificationRepository.findAll(specification, pageable);
        notifications.forEach(notification -> notification.setIsRead(true));
        return notifications.map(notificationMapper::toDto);
    }

    public void deleteNotification(Long notificationId) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        if (notification.getUser().equals(currentUser)) {
            notificationRepository.deleteById(notificationId);
        }else {
            throw new BadCredentialsException("You are not allowed to delete this notification");
        }
    }

    public void deleteNotifications(List<Long> notificationIds) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);
        notifications.forEach(notification -> {
            if (!notification.getUser().equals(currentUser)) {
                throw new BadCredentialsException("You are not allowed to delete this notification");
            }
        });
        notificationRepository.deleteAll(notifications);
    }

    public void deleteAllNotifications() {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        List<Notification> notifications = notificationRepository.findByUserId(currentUser.getId());
        notificationRepository.deleteAll(notifications);
    }
}
