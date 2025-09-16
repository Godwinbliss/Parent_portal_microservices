package com.parent.portal.Notification.service.repository;

import com.parent.portal.Notification.service.entity.Chat;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRepository extends ReactiveMongoRepository<Chat, String> { // Changed to ReactiveMongoRepository
    // Find chats involving a specific participant (will return Flux)
    Flux<Chat> findByParticipant1IdOrParticipant2Id(Long participant1Id, Long participant2Id);

    // Find a chat between two specific participants (will return Mono)
    Mono<Chat> findByParticipant1IdAndParticipant2Id(Long participant1Id, Long participant2Id);
    Mono<Chat> findByParticipant2IdAndParticipant1Id(Long participant2Id, Long participant1Id); // For reverse order
}
