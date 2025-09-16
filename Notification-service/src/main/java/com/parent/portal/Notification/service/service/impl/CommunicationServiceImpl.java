package com.parent.portal.Notification.service.service.impl;

import com.parent.portal.Notification.service.config.WebclientConfig;
import com.parent.portal.Notification.service.dto.*;
import com.parent.portal.Notification.service.entity.*;
import com.parent.portal.Notification.service.mapper.ChatMapper;
import com.parent.portal.Notification.service.mapper.NewsMapper;
import com.parent.portal.Notification.service.mapper.NotificationMapper;
import com.parent.portal.Notification.service.repository.ChatRepository;
import com.parent.portal.Notification.service.repository.NewsRepository;
import com.parent.portal.Notification.service.repository.NotificationRepository;
import com.parent.portal.Notification.service.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.annotation.KafkaListener; // For Kafka Consumer
import org.springframework.kafka.core.KafkaTemplate; // For Kafka Producer
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunicationServiceImpl implements CommunicationService {

    private final ChatRepository chatRepository;
    private final NewsRepository newsRepository;
    private final NotificationRepository notificationRepository;
    private final ChatMapper chatMapper;
    private final NewsMapper newsMapper;
    private final NotificationMapper notificationMapper;
    private final WebClient.Builder webClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${user-management-service.url}")
    private String userManagementServiceUrl;


    // Helper to fetch username from User Management Service
    private Mono<String> getUsername(Long userId) {
        if (userId == null) {
            return Mono.just("Unknown User");
        }
        return webClient.build()
                .get()
                .uri("lb://" + userManagementServiceUrl + "/api/users/{id}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("User service error: " + clientResponse.statusCode())))
                .bodyToMono(UserDto.class)
                .map(UserDto::getUsername)
                .defaultIfEmpty("User " + userId);
    }

    // Chat operations
    @Override
    public Mono<ChatDto> createChat(ChatCreateDto chatCreateDto) {
        // Check if a chat already exists between these two participants (order-agnostic)
        Mono<Chat> existingChatMono = chatRepository.findByParticipant1IdAndParticipant2Id(chatCreateDto.getParticipant1Id(), chatCreateDto.getParticipant2Id())
                .switchIfEmpty(chatRepository.findByParticipant2IdAndParticipant1Id(chatCreateDto.getParticipant1Id(), chatCreateDto.getParticipant2Id()));

        return existingChatMono
                .flatMap(chat -> Mono.<ChatDto>error(new IllegalArgumentException("Chat already exists between these participants."))) // Explicitly type Mono.error
                .switchIfEmpty(Mono.defer(() -> {
                    // Validate both participant IDs with User Management Service
                    Mono<UserDto> p1Mono = getUsername(chatCreateDto.getParticipant1Id()).map(s -> new UserDto(chatCreateDto.getParticipant1Id(), s, null, null));
                    Mono<UserDto> p2Mono = getUsername(chatCreateDto.getParticipant2Id()).map(s -> new UserDto(chatCreateDto.getParticipant2Id(), s, null, null));

                    return Mono.zip(p1Mono, p2Mono)
                            .flatMap(tuple -> {
                                Chat chat = chatMapper.chatCreateDtoToChat(chatCreateDto);
                                chat.setCreatedAt(LocalDateTime.now());
                                chat.setLastUpdatedAt(LocalDateTime.now());
                                chat.setMessages(new ArrayList<>());
                                return chatRepository.save(chat)
                                        .flatMap(savedChat -> {
                                            ChatDto dto = chatMapper.chatToChatDto(savedChat);
                                            dto.setParticipant1Username(tuple.getT1().getUsername());
                                            dto.setParticipant2Username(tuple.getT2().getUsername());
                                            return Mono.just(dto);
                                        });
                            })
                            .switchIfEmpty(Mono.<ChatDto>error(new NoSuchElementException("One or both participants not found."))); // Explicitly type Mono.error
                }));
    }

    @Override
    public Mono<ChatDto> getChatById(String id) {
        return chatRepository.findById(id)
                .flatMap(chat ->
                        Mono.zip(getUsername(chat.getParticipant1Id()), getUsername(chat.getParticipant2Id()))
                                .map(tuple -> {
                                    ChatDto dto = chatMapper.chatToChatDto(chat);
                                    dto.setParticipant1Username(tuple.getT1());
                                    dto.setParticipant2Username(tuple.getT2());
                                    dto.setMessages(chat.getMessages().stream()
                                            .map(msg -> {
                                                MessageDto msgDto = chatMapper.messageToMessageDto(msg);
                                                // For simplicity, sender username is not fetched here for each message
                                                // In a real app, you might fetch all sender usernames in one go
                                                return msgDto;
                                            })
                                            .collect(Collectors.toList()));
                                    return dto;
                                })
                )
                .switchIfEmpty(Mono.error(new NoSuchElementException("Chat not found with ID: " + id)));
    }

    @Override
    public Flux<ChatDto> getChatsByParticipantId(Long participantId) {
        return chatRepository.findByParticipant1IdOrParticipant2Id(participantId, participantId)
                .flatMap(chat ->
                        Mono.zip(getUsername(chat.getParticipant1Id()), getUsername(chat.getParticipant2Id()))
                                .map(tuple -> {
                                    ChatDto dto = chatMapper.chatToChatDto(chat);
                                    dto.setParticipant1Username(tuple.getT1());
                                    dto.setParticipant2Username(tuple.getT2());
                                    // Do not populate messages here for list view, fetch them when getting single chat
                                    dto.setMessages(null);
                                    return dto;
                                })
                );
    }

    @Override
    public Mono<MessageDto> addMessageToChat(String chatId, MessageCreateDto messageCreateDto) {
        return chatRepository.findById(chatId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Chat not found with ID: " + chatId)))
                .flatMap(chat ->
                        getUsername(messageCreateDto.getSenderId())
                                .flatMap(senderUsername -> {
                                    Message message = chatMapper.messageCreateDtoToMessage(messageCreateDto);
                                    message.setId(UUID.randomUUID().toString());
                                    message.setTimestamp(LocalDateTime.now());
                                    message.setRead(false);

                                    if (chat.getMessages() == null) {
                                        chat.setMessages(new ArrayList<>());
                                    }
                                    chat.getMessages().add(message);
                                    chat.setLastUpdatedAt(LocalDateTime.now());

                                    return chatRepository.save(chat)
                                            .map(savedChat -> {
                                                MessageDto dto = chatMapper.messageToMessageDto(message);
                                                dto.setSenderUsername(senderUsername);
                                                kafkaTemplate.send("new-message-events", dto);
                                                return dto;
                                            });
                                })
                );
    }

    @Override
    public Mono<ChatDto> markMessagesAsRead(String chatId, Long userId) {
        return chatRepository.findById(chatId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Chat not found with ID: " + chatId)))
                .flatMap(chat -> {
                    boolean changed = false;
                    for (Message message : chat.getMessages()) {
                        if (!message.getSenderId().equals(userId) && !message.isRead()) {
                            message.setRead(true);
                            changed = true;
                        }
                    }
                    if (changed) {
                        return chatRepository.save(chat)
                                .flatMap(savedChat -> getChatById(savedChat.getId()));
                    } else {
                        return getChatById(chat.getId());
                    }
                });
    }

    // News operations
    @Override
    public Mono<NewsDto> createNews(NewsCreateDto newsCreateDto) {
        return getUsername(newsCreateDto.getAuthorId())
                .flatMap(authorUsername -> {
                    News news = newsMapper.newsCreateDtoToNews(newsCreateDto);
                    news.setPublishedDate(LocalDateTime.now());
                    return newsRepository.save(news)
                            .flatMap(savedNews -> {
                                NewsDto dto = newsMapper.newsToNewsDto(savedNews);
                                dto.setAuthorUsername(authorUsername);
                                kafkaTemplate.send("news-events", dto);
                                return Mono.just(dto);
                            });
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Author user not found with ID: " + newsCreateDto.getAuthorId())));
    }

    @Override
    public Mono<NewsDto> getNewsById(String id) {
        return newsRepository.findById(id)
                .flatMap(news ->
                        getUsername(news.getAuthorId())
                                .map(authorUsername -> {
                                    NewsDto dto = newsMapper.newsToNewsDto(news);
                                    dto.setAuthorUsername(authorUsername);
                                    return dto;
                                })
                )
                .switchIfEmpty(Mono.error(new NoSuchElementException("News not found with ID: " + id)));
    }

    @Override
    public Flux<NewsDto> getAllNews() {
        return newsRepository.findAll()
                .flatMap(news ->
                        getUsername(news.getAuthorId())
                                .map(authorUsername -> {
                                    NewsDto dto = newsMapper.newsToNewsDto(news);
                                    dto.setAuthorUsername(authorUsername);
                                    return dto;
                                })
                );
    }

    @Override
    public Flux<NewsDto> getNewsByCategory(String category) {
        return newsRepository.findByCategory(category)
                .flatMap(news ->
                        getUsername(news.getAuthorId())
                                .map(authorUsername -> {
                                    NewsDto dto = newsMapper.newsToNewsDto(news);
                                    dto.setAuthorUsername(authorUsername);
                                    return dto;
                                })
                );
    }

    @Override
    public Mono<Void> deleteNews(String id) {
        return newsRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("News not found with ID: " + id)))
                .flatMap(newsRepository::delete);
    }

    // Notification operations
    @Override
    public Mono<NotificationDto> createNotification(NotificationCreateDto notificationCreateDto) {
        return getUsername(notificationCreateDto.getRecipientId())
                .flatMap(recipientUsername -> {
                    Notification notification = notificationMapper.notificationCreateDtoToNotification(notificationCreateDto);
                    notification.setSentDate(LocalDateTime.now());
                    notification.setRead(false);
                    return notificationRepository.save(notification)
                            .flatMap(savedNotification -> {
                                NotificationDto dto = notificationMapper.notificationToNotificationDto(savedNotification);
                                dto.setRecipientUsername(recipientUsername);
                                return Mono.just(dto);
                            });
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Recipient user not found with ID: " + notificationCreateDto.getRecipientId())));
    }

    @Override
    public Mono<NotificationDto> getNotificationById(String id) {
        return notificationRepository.findById(id)
                .flatMap(notification ->
                        getUsername(notification.getRecipientId())
                                .map(recipientUsername -> {
                                    NotificationDto dto = notificationMapper.notificationToNotificationDto(notification);
                                    dto.setRecipientUsername(recipientUsername);
                                    return dto;
                                })
                )
                .switchIfEmpty(Mono.error(new NoSuchElementException("Notification not found with ID: " + id)));
    }

    @Override
    public Flux<NotificationDto> getNotificationsByRecipientId(Long recipientId) {
        return notificationRepository.findByRecipientId(recipientId)
                .flatMap(notification ->
                        getUsername(notification.getRecipientId())
                                .map(recipientUsername -> {
                                    NotificationDto dto = notificationMapper.notificationToNotificationDto(notification);
                                    dto.setRecipientUsername(recipientUsername);
                                    return dto;
                                })
                );
    }

    @Override
    public Flux<NotificationDto> getUnreadNotificationsByRecipientId(Long recipientId) {
        return notificationRepository.findByRecipientIdAndRead(recipientId, false)
                .flatMap(notification ->
                        getUsername(notification.getRecipientId())
                                .map(recipientUsername -> {
                                    NotificationDto dto = notificationMapper.notificationToNotificationDto(notification);
                                    dto.setRecipientUsername(recipientUsername);
                                    return dto;
                                })
                );
    }

    @Override
    public Mono<NotificationDto> markNotificationAsRead(String id) {
        return notificationRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Notification not found with ID: " + id)))
                .flatMap(notification -> {
                    if (!notification.isRead()) {
                        notification.setRead(true);
                        return notificationRepository.save(notification)
                                .flatMap(savedNotification -> getNotificationById(savedNotification.getId()));
                    } else {
                        return getNotificationById(notification.getId());
                    }
                });
    }

    @Override
    public Mono<Void> deleteNotification(String id) {
        return notificationRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Notification not found with ID: " + id)))
                .flatMap(notificationRepository::delete);
    }

    // Kafka Listener for Payment Confirmation (Example)
    @Override
    @KafkaListener(topics = "payment-events", groupId = "communication-service-group")
    public void handlePaymentConfirmationEvent(Long parentUserId, Long studentId, String transactionId, String status) {
        String messageContent = String.format("Payment for student %d (Transaction ID: %s) is %s.", studentId, transactionId, status);
        NotificationCreateDto notificationCreateDto = new NotificationCreateDto(
                parentUserId,
                messageContent,
                NotificationType.PAYMENT_CONFIRMATION,
                transactionId
        );
        createNotification(notificationCreateDto).subscribe(
                notificationDto -> System.out.println("Created notification for payment: " + notificationDto.getMessage()),
                error -> System.err.println("Failed to create notification for payment: " + error.getMessage())
        );
    }
}