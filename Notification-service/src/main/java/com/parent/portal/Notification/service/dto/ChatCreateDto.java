package com.parent.portal.Notification.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatCreateDto {
    @NotNull(message = "Participant 1 ID is required")
    private Long participant1Id;
    @NotNull(message = "Participant 2 ID is required")
    private Long participant2Id;
}
