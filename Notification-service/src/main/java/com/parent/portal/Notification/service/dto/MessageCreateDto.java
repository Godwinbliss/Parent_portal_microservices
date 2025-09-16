package com.parent.portal.Notification.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreateDto {
    @NotNull(message = "Sender ID is required")
    private Long senderId;
    @NotBlank(message = "Message content cannot be empty")
    private String content;
}
