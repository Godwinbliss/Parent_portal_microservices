package com.parent.portal.user_management_service.security;

import com.parent.portal.user_management_service.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SpringSecurityConfig {
        /**
         * Configures the security filter chain.
         * We're using HTTP Basic authentication for stateless API security.
         * All endpoints are secured, but we'll use method-level security in the controllers
         * to refine access later.
         * @param http The HttpSecurity object to configure.
         * @return The configured SecurityFilterChain.
         * @throws Exception If an error occurs during configuration.
//         */
//        @Bean
//        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//            http
//                    .csrf(AbstractHttpConfigurer::disable)
//                    .authorizeHttpRequests(authorize -> authorize
//                            // Allow unauthenticated access to the user creation endpoint
//                            .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
//                            .requestMatchers("/api/users/login", "/api/users/register").permitAll()
//                            .requestMatchers("/actuator/**", "/v3/api-docs/**").permitAll()
//                            // Require ADMIN authority for GET requests to /api/users
//                           // .requestMatchers("/api/users/**").hasRole("ADMIN")
//
//                            // Require authentication for all other requests
//                            //.anyRequest().authenticated()
//                    )
//                    .httpBasic(httpBasic -> httpBasic
//                            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//                    )
//                    .sessionManagement(session -> session.disable());
//
//            return http.build();
//        }
//
//        /**
//         * Configures a UserDetailsService to load user details from the database.
//         * This bean is used by Spring Security's authentication manager.
//         * @param userRepository The UserRepository to fetch user data.
//         * @return A UserDetailsService implementation.
//         */
//        @Bean
//        public UserDetailsService userDetailsService(UserRepository userRepository) {
//            return email -> {
//                com.parent.portal.user_management_service.entity.User user = userRepository.findByEmail(email)
//                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//
//                // Build a UserDetails object from the User entity
//                return User.builder()
//                        .username(user.getEmail())
//                        .password(user.getPassword())
//                        .authorities(user.getRole().name()) // Use the role name as authority
//                        .build();
//            };
//        }

        /**
         * Configures a password encoder for hashing passwords.
         * BCrypt is a standard, secure hashing algorithm recommended for this purpose.
         * @return A BCryptPasswordEncoder bean.
         */
        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
}
