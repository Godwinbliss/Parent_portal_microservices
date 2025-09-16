package com.parent.portal.Notification.service.mapper;

import com.parent.portal.Notification.service.dto.NotificationCreateDto;
import com.parent.portal.Notification.service.dto.NotificationDto;
import com.parent.portal.Notification.service.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(target = "recipientUsername", ignore = true) // Will be populated by service
    NotificationDto notificationToNotificationDto(Notification notification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sentDate", ignore = true) // Will be set by service
    @Mapping(target = "read", constant = "false") // New notifications are unread by default
    Notification notificationCreateDtoToNotification(NotificationCreateDto notificationCreateDto);

    List<NotificationDto> notificationListToNotificationDtoList(List<Notification> notifications);
}
