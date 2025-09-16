package com.parent.portal.user_management_service.service;

import com.parent.portal.user_management_service.dto.UserCreateDto;
import com.parent.portal.user_management_service.dto.UserDto;
import com.parent.portal.user_management_service.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserCreateDto userCreateDto);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, UserUpdateDto userUpdateDto);
    UserDto findByEmail(String email);
    void deleteUser(Long id);
}

