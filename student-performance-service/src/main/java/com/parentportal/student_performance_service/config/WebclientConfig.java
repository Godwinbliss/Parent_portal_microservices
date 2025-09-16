package com.parentportal.student_performance_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebclientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() { // NEWLY ADDED
        return WebClient.builder(); // NEWLY ADDED
    }
}
