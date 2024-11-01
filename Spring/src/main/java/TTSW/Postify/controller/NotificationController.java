package TTSW.Postify.controller;

import TTSW.Postify.dto.NotificationDTO;
import TTSW.Postify.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/list")
    public ResponseEntity<Page<NotificationDTO>> getNotifications(Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotifications(pageable));
    }

    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationDTO>> getUnreadNotifications(Pageable pageable) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(pageable));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/list")
    public ResponseEntity<?> deleteNotifications(@RequestBody List<Long> ids) {
        notificationService.deleteNotifications(ids);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<?> deleteAllNotifications() {
        notificationService.deleteAllNotifications();
        return ResponseEntity.ok().build();
    }
}
