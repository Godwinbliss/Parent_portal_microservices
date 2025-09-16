package com.parent.portal.Notification.service.dto;

import com.parent.portal.Notification.service.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private String id;
    private Long recipientId;
    private String recipientUsername; // To be populated from User Service
    private String message;
    private LocalDateTime sentDate;
    private boolean read;
    private NotificationType type;
    private String relatedEntityId;
}
