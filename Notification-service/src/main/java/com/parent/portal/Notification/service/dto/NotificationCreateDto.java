package com.parent.portal.Notification.service.dto;

import com.parent.portal.Notification.service.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateDto {
    @NotNull(message = "Recipient ID is required")
    private Long recipientId;
    @NotBlank(message = "Message content is required")
    private String message;
    @NotNull(message = "Notification type is required")
    private NotificationType type;
    private String relatedEntityId; // Optional
}