package com.parent.portal.Notification.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String id;
    private Long senderId;
    private String senderUsername; // To be populated from User Service
    private String content;
    private LocalDateTime timestamp;
    private boolean read;
}