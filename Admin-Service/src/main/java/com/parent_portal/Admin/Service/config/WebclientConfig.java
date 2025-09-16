package com.parent_portal.Admin.Service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebclientConfig {

    /**
     * Configures and provides a WebClient bean for making HTTP requests to other services.
     * This is a reactive, non-blocking HTTP client.
     * @return A configured WebClient instance.
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
