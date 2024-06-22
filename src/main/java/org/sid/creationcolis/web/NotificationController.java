package org.sid.creationcolis.web;

import org.sid.creationcolis.entities.NotificationInterne;
import org.sid.creationcolis.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }




    @GetMapping("/unread-count")
    public long getUnreadCount() {
        return notificationService.countUnreadNotifications();
    }
    @GetMapping
    public List<NotificationInterne> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @PostMapping
    public ResponseEntity<NotificationInterne> createNotification(@RequestBody String message) {
        NotificationInterne notification = notificationService.createNotification(message);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/mark-as-read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-as-unread/{id}")
    public ResponseEntity<Void> markAsUnread(@PathVariable Long id) {
        notificationService.markAsUnread(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}
