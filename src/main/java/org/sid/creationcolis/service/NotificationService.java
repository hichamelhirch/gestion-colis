package org.sid.creationcolis.service;

import org.sid.creationcolis.entities.NotificationInterne;
import org.sid.creationcolis.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationInterne createNotification(String message) {
        NotificationInterne notification = NotificationInterne.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    public List<NotificationInterne> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void markAsRead(Long id) {
        NotificationInterne notification = notificationRepository.findById(id).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAsUnread(Long id) {
        NotificationInterne notification = notificationRepository.findById(id).orElseThrow();
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public long countUnreadNotifications() {
        return notificationRepository.countByIsReadFalse();
    }
}
