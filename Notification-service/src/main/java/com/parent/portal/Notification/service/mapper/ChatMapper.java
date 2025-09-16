package com.parent.portal.Notification.service.mapper;

import com.parent.portal.Notification.service.dto.ChatCreateDto;
import com.parent.portal.Notification.service.dto.ChatDto;
import com.parent.portal.Notification.service.dto.MessageCreateDto;
import com.parent.portal.Notification.service.dto.MessageDto;
import com.parent.portal.Notification.service.entity.Chat;
import com.parent.portal.Notification.service.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

    @Mapping(target = "participant1Username", ignore = true) // Will be populated by service
    @Mapping(target = "participant2Username", ignore = true) // Will be populated by service
    ChatDto chatToChatDto(Chat chat);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "messages", ignore = true)
    Chat chatCreateDtoToChat(ChatCreateDto chatCreateDto);

    @Mapping(target = "senderUsername", ignore = true) // Will be populated by service
    MessageDto messageToMessageDto(Message message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "read", constant = "false") // New messages are unread by default
    Message messageCreateDtoToMessage(MessageCreateDto messageCreateDto);

    List<ChatDto> chatListToChatDtoList(List<Chat> chats);
    List<MessageDto> messageListToMessageDtoList(List<Message> messages);
}
