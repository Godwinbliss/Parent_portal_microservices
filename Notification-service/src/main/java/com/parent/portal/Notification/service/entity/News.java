package com.parent.portal.Notification.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "news")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Id
    private String id;
    private String title;
    private String content;
    private LocalDateTime publishedDate;
    private Long authorId; // User ID of the admin who posted
    private String category; // e.g., "General", "Academic", "Events"
}
