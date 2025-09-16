package com.parent.portal.Notification.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    private String id; // MongoDB uses String for _id by default
    private Long participant1Id; // User ID from User Management Service
    private Long participant2Id; // User ID from User Management Service
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private List<Message> messages; // Embedded messages
}