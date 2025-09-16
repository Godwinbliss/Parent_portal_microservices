package com.parent.portal.Notification.service.repository;

import com.parent.portal.Notification.service.entity.News;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NewsRepository extends ReactiveMongoRepository<News, String> { // Changed to ReactiveMongoRepository
    Flux<News> findByAuthorId(Long authorId);
    Flux<News> findByCategory(String category);
}
