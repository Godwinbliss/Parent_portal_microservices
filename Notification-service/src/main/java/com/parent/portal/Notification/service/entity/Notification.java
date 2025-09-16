package com.parent.portal.Notification.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    private String id;
    private Long recipientId; // User ID who receives the notification
    private String message;
    private LocalDateTime sentDate;
    private boolean read;
    private NotificationType type; // e.g., PAYMENT_CONFIRMATION, NEW_MESSAGE, ANNOUNCEMENT
    private String relatedEntityId; // Optional: ID of related entity (e.g., paymentId, chatId)
}
