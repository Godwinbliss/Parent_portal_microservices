package com.parent.portal.Notification.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private String id;
    private Long participant1Id;
    private Long participant2Id;
    private String participant1Username; // To be populated from User Service
    private String participant2Username; // To be populated from User Service
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private List<MessageDto> messages;
}
