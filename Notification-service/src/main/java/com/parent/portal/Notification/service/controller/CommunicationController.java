package com.parent.portal.Notification.service.controller;

import com.parent.portal.Notification.service.dto.*;
import com.parent.portal.Notification.service.service.CommunicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/communication")
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationService communicationService;


    // Chat Endpoints
    /**
     * Creates a new chat between two participants.
     * @param chatCreateDto DTO containing participant IDs.
     * @return The created chat as a DTO.
     */
    @PostMapping("/chats")
    public Mono<ResponseEntity<ChatDto>> createChat(@Valid @RequestBody ChatCreateDto chatCreateDto) {
        return communicationService.createChat(chatCreateDto)
                .map(chatDto -> new ResponseEntity<>(chatDto, HttpStatus.CREATED))
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)))
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.CONFLICT))); // Chat already exists
    }

    /**
     * Retrieves a chat by its ID.
     * @param id The ID of the chat.
     * @return The chat as a DTO.
     */
    @GetMapping("/chats/{id}")
    public Mono<ResponseEntity<ChatDto>> getChatById(@PathVariable String id) {
        return communicationService.getChatById(id)
                .map(chatDto -> new ResponseEntity<>(chatDto, HttpStatus.OK))
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    /**
     * Retrieves all chats involving a specific participant.
     * @param participantId The ID of the participant.
     * @return A Flux of chat DTOs.
     */
    @GetMapping("/chats/byParticipant/{participantId}")
    public ResponseEntity<Flux<ChatDto>> getChatsByParticipantId(@PathVariable Long participantId) {
        Flux<ChatDto> chats = communicationService.getChatsByParticipantId(participantId);
        return new ResponseEntity<>(chats, HttpStatus.OK);
    }

    /**
     * Adds a new message to an existing chat.
     * @param chatId The ID of the chat.
     * @param messageCreateDto DTO containing message details.
     * @return The created message as a DTO.
     */
    @PostMapping("/chats/{chatId}/messages")
    public Mono<ResponseEntity<MessageDto>> addMessageToChat(@PathVariable String chatId, @Valid @RequestBody MessageCreateDto messageCreateDto) {
        return communicationService.addMessageToChat(chatId, messageCreateDto)
                .map(messageDto -> new ResponseEntity<>(messageDto, HttpStatus.CREATED))
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    /**
     * Marks messages in a chat as read for a specific user.
     * @param chatId The ID of the chat.
     * @param userId The ID of the user marking messages as read.
     * @return The updated chat as a DTO.
     */
    @PatchMapping("/chats/{chatId}/read/{userId}")
    public Mono<ResponseEntity<ChatDto>> markMessagesAsRead(@PathVariable String chatId, @PathVariable Long userId) {
        return communicationService.markMessagesAsRead(chatId, userId)
                .map(chatDto -> new ResponseEntity<>(chatDto, HttpStatus.OK))
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    // News Endpoints
    /**
     * Creates a new news announcement.
     * @param newsCreateDto DTO containing news details.
     * @return The created news as a DTO.
     */
    @PostMapping("/news")
    public Mono<ResponseEntity<NewsDto>> createNews(@Valid @RequestBody NewsCreateDto newsCreateDto) {
        return communicationService.createNews(newsCreateDto)
                .map(newsDto -> new ResponseEntity<>(newsDto, HttpStatus.CREATED))
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND))); // Author not found
    }

    /**
     * Retrieves a news announcement by its ID.
     * @param id The ID of the news.
     * @return The news as a DTO.
     */
    @GetMapping("/news/{id}")
    public Mono<ResponseEntity<NewsDto>> getNewsById(@PathVariable String id) {
        return communicationService.getNewsById(id)
                .map(newsDto -> new ResponseEntity<>(newsDto, HttpStatus.OK))
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    /**
     * Retrieves all news announcements.
     * @return A Flux of news DTOs.
     */
    @GetMapping("/news")
    public ResponseEntity<Flux<NewsDto>> getAllNews() {
        Flux<NewsDto> news = communicationService.getAllNews();
        return new ResponseEntity<>(news, HttpStatus.OK);
    }

    /**
     * Retrieves news announcements by category.
     * @param category The category of news.
     * @return A Flux of news DTOs.
     */
    @GetMapping("/news/byCategory")
    public ResponseEntity<Flux<NewsDto>> getNewsByCategory(@RequestParam String category) {
        Flux<NewsDto> news = communicationService.getNewsByCategory(category);
        return new ResponseEntity<>(news, HttpStatus.OK);
    }

    /**
     * Deletes a news announcement by its ID.
     * @param id The ID of the news to delete.
     * @return HTTP status 204 (No Content) on success.
     */
    @DeleteMapping("/news/{id}")
    public Mono<ResponseEntity<Object>> deleteNews(@PathVariable String id) {
        return communicationService.deleteNews(id)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)))
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    // Notification Endpoints
    /**
     * Creates a new notification.
     * @param notificationCreateDto DTO containing notification details.
     * @return The created notification as a DTO.
     */
    @PostMapping("/notifications")
    public Mono<ResponseEntity<NotificationDto>> createNotification(@Valid @RequestBody NotificationCreateDto notificationCreateDto) {
        return communicationService.createNotification(notificationCreateDto)
                .map(notificationDto -> new ResponseEntity<>(notificationDto, HttpStatus.CREATED))
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND))); // Recipient not found
    }

    /**
     * Retrieves a notification by its ID.
     * @param id The ID of the notification.
     * @return The notification as a DTO.
     */
    @GetMapping("/notifications/{id}")
    public Mono<ResponseEntity<NotificationDto>> getNotificationById(@PathVariable String id) {
        return communicationService.getNotificationById(id)
                .map(notificationDto -> new ResponseEntity<>(notificationDto, HttpStatus.OK))
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    /**
     * Retrieves all notifications for a specific recipient.
     * @param recipientId The ID of the recipient.
     * @return A Flux of notification DTOs.
     */
    @GetMapping("/notifications/byRecipient/{recipientId}")
    public ResponseEntity<Flux<NotificationDto>> getNotificationsByRecipientId(@PathVariable Long recipientId) {
        Flux<NotificationDto> notifications = communicationService.getNotificationsByRecipientId(recipientId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    /**
     * Retrieves unread notifications for a specific recipient.
     * @param recipientId The ID of the recipient.
     * @return A Flux of unread notification DTOs.
     */
    @GetMapping("/notifications/byRecipient/{recipientId}/unread")
    public ResponseEntity<Flux<NotificationDto>> getUnreadNotificationsByRecipientId(@PathVariable Long recipientId) {
        Flux<NotificationDto> notifications = communicationService.getUnreadNotificationsByRecipientId(recipientId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    /**
     * Marks a specific notification as read.
     * @param id The ID of the notification to mark as read.
     * @return The updated notification as a DTO.
     */
    @PatchMapping("/notifications/{id}/read")
    public Mono<ResponseEntity<NotificationDto>> markNotificationAsRead(@PathVariable String id) {
        return communicationService.markNotificationAsRead(id)
                .map(notificationDto -> new ResponseEntity<>(notificationDto, HttpStatus.OK))
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    /**
     * Deletes a notification by its ID.
     * @param id The ID of the notification to delete.
     * @return HTTP status 204 (No Content) on success.
     */
    @DeleteMapping("/notifications/{id}")
    public Mono<ResponseEntity<Object>> deleteNotification(@PathVariable String id) {
        return communicationService.deleteNotification(id)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)))
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }
}
