package com.parent.portal.Notification.service.repository;

import com.parent.portal.Notification.service.entity.Notification;
import com.parent.portal.Notification.service.entity.NotificationType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> { // Changed to ReactiveMongoRepository
    Flux<Notification> findByRecipientId(Long recipientId);
    Flux<Notification> findByRecipientIdAndRead(Long recipientId, boolean read);
    Flux<Notification> findByRecipientIdAndType(Long recipientId, NotificationType type);
}
