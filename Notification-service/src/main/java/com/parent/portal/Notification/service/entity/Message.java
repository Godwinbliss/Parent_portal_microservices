package com.parent.portal.Notification.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id; // Unique ID for the message within the chat
    private Long senderId; // User ID of the sender
    private String content;
    private LocalDateTime timestamp;
    private boolean read;
}
