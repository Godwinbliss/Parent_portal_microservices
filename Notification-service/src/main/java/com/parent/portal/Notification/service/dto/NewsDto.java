package com.parent.portal.Notification.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto {

    private String id;
    private String title;
    private String content;
    private LocalDateTime publishedDate;
    private Long authorId;
    private String authorUsername; // To be populated from User Service
    private String category;
}
