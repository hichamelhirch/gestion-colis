package org.sid.creationcolis.repository;

import org.sid.creationcolis.entities.NotificationInterne;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationInterne,Long> {
    long countByIsReadFalse();
}
