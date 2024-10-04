package TTSW.Postify.service;

import TTSW.Postify.dto.NotificationDTO;
import TTSW.Postify.mapper.NotificationMapper;
import TTSW.Postify.model.Notification;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
