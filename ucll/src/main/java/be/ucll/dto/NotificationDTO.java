package be.ucll.dto;

import java.time.LocalDateTime;

public record NotificationDTO(
        Long id,
        String title,
        String message,
        LocalDateTime timestamp,
        boolean isRead,
        Long relatedItemId

) {
}
