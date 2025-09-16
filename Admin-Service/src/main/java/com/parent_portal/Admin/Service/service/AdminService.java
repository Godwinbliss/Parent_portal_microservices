package com.parent_portal.Admin.Service.service;

import com.parent_portal.Admin.Service.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminService {

    Flux<UserDto> getAllUsers(); // Reactive stream of users
    Mono<UserDto> getUserById(Long id); // Reactive single user
    Mono<String> postNews(String newsContent); // Placeholder for posting news
}
