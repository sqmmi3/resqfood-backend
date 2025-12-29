package be.ucll.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import be.ucll.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Fetch all notification for user, sorted by newest first
    List<Notification> findByUser_UsernameOrderByTimestampDesc(String username);

    long countByUser_UsernameAndIsReadFalse(String username);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.username = :username AND n.isRead = false")
    void markAllAsRead(@Param("username") String username);
}
