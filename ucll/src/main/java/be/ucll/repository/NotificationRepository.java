package be.ucll.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import be.ucll.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Fetch all notification for user, sorted by newest first
    List<Notification> findByUser_UsernameOrderByTimestampDesc(String username);

    long countByUser_UsernameAndIsReadFalse(String username);
}
