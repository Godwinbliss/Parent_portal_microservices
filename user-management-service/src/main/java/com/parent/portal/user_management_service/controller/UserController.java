package com.parent.portal.user_management_service.controller;
 // Import the mapper

import com.parent.portal.user_management_service.dto.UserCreateDto;
import com.parent.portal.user_management_service.dto.UserDto;
import com.parent.portal.user_management_service.dto.UserUpdateDto;
import com.parent.portal.user_management_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.NoSuchElementException; // Import NoSuchElementException

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService; // Inject UserService

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Authenticates a user and returns their details.
     * This endpoint is secured by Spring Security's HTTP Basic Auth, and the UserDetailsService now
     * authenticates by email.
     */
//    @PostMapping("/login")
//    public ResponseEntity<UserDto> login() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//
//        String email = authentication.getName(); // Spring Security's principal name is now the email
//        try {
//            UserDto user = userService.findByEmail(email);
//            return new ResponseEntity<>(user, HttpStatus.OK);
//        } catch (NoSuchElementException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    /**
     * Creates a new user.
     * @param userCreateDto The DTO containing user details for creation.
     * @return The created user as a DTO with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        UserDto createdUser = userService.createUser(userCreateDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Retrieves all users.
     * @return A list of all users as DTOs.
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/byEmail/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        try {
            UserDto user = userService.findByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves a user by their ID.
     * @param id The ID of the user.
     * @return The user as a DTO if found, or HTTP status 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        try {
            UserDto user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates an existing user.
     * @param id The ID of the user to update.
     * @param userUpdateDto The DTO containing updated user details.
     * @return The updated user as a DTO if found, or HTTP status 404 (Not Found).
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        try {
            UserDto updatedUser = userService.updateUser(id, userUpdateDto);
            return new ResponseEntity<>(updatedUser, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a user by their ID.
     * @param id The ID of the user to delete.
     * @return HTTP status 204 (No Content) on successful deletion, or 404 (Not Found) if user does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
