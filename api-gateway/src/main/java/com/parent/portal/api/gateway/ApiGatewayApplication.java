package com.parent.portal.api.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	// Injecting service names (which will be resolved by Eureka)
	// These values correspond to the 'spring.application.name' of each microservice
	@Value("${user-management-service.name}")
	private String userManagementServiceName;
	@Value("${student-performance-service.name}")
	private String studentPerformanceServiceName;
	@Value("${payment-service.name}")
	private String paymentServiceName;
	@Value("${communication-service.name}")
	private String communicationServiceName;
	@Value("${admin-service.name}")
	private String adminServiceName;


	/**
	 * Configures the routing rules for the API Gateway.
	 * This bean defines how incoming requests are mapped to different microservices,
	 * using service discovery for load balancing.
	 * It also routes requests for the raw OpenAPI JSON definitions.
	 * @param builder The RouteLocatorBuilder to create routes.
	 * @return A configured RouteLocator.
	 */
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// Route for User Management Service API
				.route("user_management_service", r -> r.path("/api/users/**")
						.uri("lb://" + userManagementServiceName))
				// Route for User Management Service OpenAPI JSON definition
				.route("user_management_api_docs", r -> r.path("/v3/api-docs/user-service") // Unique path for docs
						.filters(f -> f.rewritePath("/v3/api-docs/user-service", "/v3/api-docs")) // Rewrite to microservice's actual docs path
						.uri("lb://" + userManagementServiceName))

				// Route for Student Performance Service API
				.route("student_performance_service", r -> r.path("/api/students/**")
						.uri("lb://" + studentPerformanceServiceName))
				// Route for Student Performance Service OpenAPI JSON definition
				.route("student_performance_api_docs", r -> r.path("/v3/api-docs/student-service")
						.filters(f -> f.rewritePath("/v3/api-docs/student-service", "/v3/api-docs"))
						.uri("lb://" + studentPerformanceServiceName))

				// Route for Payment Service API
				.route("payment_service", r -> r.path("/api/payments/**")
						.uri("lb://" + paymentServiceName))
				// Route for Payment Service OpenAPI JSON definition
				.route("payment_api_docs", r -> r.path("/v3/api-docs/payment-service")
						.filters(f -> f.rewritePath("/v3/api-docs/payment-service", "/v3/api-docs"))
						.uri("lb://" + paymentServiceName))

				// Route for Communication/Notification Service API
				.route("communication_service", r -> r.path("/api/communication/**")
						.uri("lb://" + communicationServiceName))
				// Route for Communication/Notification Service OpenAPI JSON definition
				.route("communication_api_docs", r -> r.path("/v3/api-docs/communication-service")
						.filters(f -> f.rewritePath("/v3/api-docs/communication-service", "/v3/api-docs"))
						.uri("lb://" + communicationServiceName))

				// Route for Admin Service API
				.route("admin_service", r -> r.path("/api/admin/**")
						.uri("lb://" + adminServiceName))
				// Route for Admin Service OpenAPI JSON definition
				.route("admin_api_docs", r -> r.path("/v3/api-docs/admin-service")
						.filters(f -> f.rewritePath("/v3/api-docs/admin-service", "/v3/api-docs"))
						.uri("lb://" + adminServiceName))
				.build();
	}

}
