package com.parent_portal.Admin.Service.service.impl;

import com.parent_portal.Admin.Service.dto.UserDto;
import com.parent_portal.Admin.Service.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AdminServiceImpl implements AdminService {

    private final WebClient webClient;

    // Injecting the service name (as registered in Eureka) instead of a direct URL
    @Value("${user-management-service.name}")
    private String userManagementServiceName;

    // Placeholder for Communication Service Name
    @Value("${communication-service.name}")
    private String communicationServiceName;

    @Autowired
    public AdminServiceImpl(WebClient.Builder webClientBuilder) {
        // The injected webClientBuilder is already @LoadBalanced
        this.webClient = webClientBuilder.build();
    }

    /**
     * Retrieves all users from the User Management Service.
     * Uses WebClient for non-blocking communication, resolving service via Eureka.
     * @return A Flux of UserDto objects.
     */
    @Override
    public Flux<UserDto> getAllUsers() {
        // Use "http://" + serviceName for load-balanced WebClient calls
        return webClient.get()
                .uri("lb://" + userManagementServiceName + "/api/users")
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        Mono.error(new RuntimeException("Error fetching users: " + clientResponse.statusCode())))
                .onStatus(status -> status.is5xxServerError(), clientResponse ->
                        Mono.error(new RuntimeException("Server error fetching users: " + clientResponse.statusCode())))
                .bodyToFlux(UserDto.class); // Expecting a Flux of UserDto
    }

    /**
     * Retrieves a single user by ID from the User Management Service.
     * @param id The ID of the user to retrieve.
     * @return A Mono of UserDto object.
     */
    @Override
    public Mono<UserDto> getUserById(Long id) {
        return webClient.get()
                .uri("lb://" + userManagementServiceName + "/api/users/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        Mono.error(new RuntimeException("Error fetching user: " + clientResponse.statusCode())))
                .onStatus(status -> status.is5xxServerError(), clientResponse ->
                        Mono.error(new RuntimeException("Server error fetching user: " + clientResponse.statusCode())))
                .bodyToMono(UserDto.class); // Expecting a Mono of UserDto
    }

    /**
     * Placeholder method to post news to the Communication/Notification Service.
     * In a real implementation, this would send a request to the Communication Service.
     * @param newsContent The content of the news to post.
     * @return A Mono indicating success or failure.
     */
    @Override
    public Mono<String> postNews(String newsContent) {
        // Example of calling Communication Service via Eureka
         return webClient.post()
                 .uri("lb://" + communicationServiceName + "/api/communication/news")
                 .bodyValue(newsContent)
                 .retrieve()
                 .bodyToMono(String.class);
        //return Mono.just("News '" + newsContent + "' simulated to be posted to Communication Service.");
    }
}