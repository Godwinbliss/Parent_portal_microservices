package com.parent.portal.Notification.service.service;

import com.parent.portal.Notification.service.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommunicationService {
    // Chat operations
    Mono<ChatDto> createChat(ChatCreateDto chatCreateDto);
    Mono<ChatDto> getChatById(String id);
    Flux<ChatDto> getChatsByParticipantId(Long participantId);
    Mono<MessageDto> addMessageToChat(String chatId, MessageCreateDto messageCreateDto);
    Mono<ChatDto> markMessagesAsRead(String chatId, Long userId);

    // News operations
    Mono<NewsDto> createNews(NewsCreateDto newsCreateDto);
    Mono<NewsDto> getNewsById(String id);
    Flux<NewsDto> getAllNews();
    Flux<NewsDto> getNewsByCategory(String category);
    Mono<Void> deleteNews(String id);

    // Notification operations
    Mono<NotificationDto> createNotification(NotificationCreateDto notificationCreateDto);
    Mono<NotificationDto> getNotificationById(String id);
    Flux<NotificationDto> getNotificationsByRecipientId(Long recipientId);
    Flux<NotificationDto> getUnreadNotificationsByRecipientId(Long recipientId);
    Mono<NotificationDto> markNotificationAsRead(String id);
    Mono<Void> deleteNotification(String id);

    // Kafka Listener for Payment Confirmation (Example)
    void handlePaymentConfirmationEvent(Long parentUserId, Long studentId, String transactionId, String status);
}
