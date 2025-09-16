package com.parent_portal.Admin.Service.controller;

import com.parent_portal.Admin.Service.dto.UserDto;
import com.parent_portal.Admin.Service.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Retrieves all users (parents/admins) by calling the User Management Service.
     * @return A Flux of UserDto objects wrapped in a ResponseEntity.
     */
    @GetMapping("/users")
    public ResponseEntity<Flux<UserDto>> getAllUsers() {
        Flux<UserDto> users = adminService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Retrieves a single user by ID by calling the User Management Service.
     * @param id The ID of the user.
     * @return A Mono of UserDto object wrapped in a ResponseEntity.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Mono<UserDto>> getUserById(@PathVariable Long id) {
        Mono<UserDto> user = adminService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Endpoint for an admin to post news. This is a placeholder that simulates
     * sending news to the Communication/Notification Service.
     * @param newsContent The content of the news to be posted.
     * @return A Mono of String indicating the result of the operation.
     */
    @PostMapping("/news")
    public ResponseEntity<Mono<String>> postNews(@RequestBody String newsContent) {
        Mono<String> response = adminService.postNews(newsContent);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}