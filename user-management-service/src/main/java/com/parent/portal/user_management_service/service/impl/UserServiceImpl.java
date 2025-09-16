package com.parent.portal.user_management_service.service.impl;

import com.parent.portal.user_management_service.dto.UserCreateDto;
import com.parent.portal.user_management_service.dto.UserDto;
import com.parent.portal.user_management_service.dto.UserUpdateDto;
import com.parent.portal.user_management_service.entity.User;
import com.parent.portal.user_management_service.mapper.UserMapper;
import com.parent.portal.user_management_service.repository.UserRepository;
import com.parent.portal.user_management_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException; // For handling not found cases

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto userCreateDto) {
        User user = userMapper.userCreateDtoToUser(userCreateDto);
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.userToUserDto(savedUser);
    }


    @Override
    @Transactional(readOnly = true)
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
        return userMapper.userToUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.userListToUserDtoList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));
        return userMapper.userToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));

        userMapper.updateUserFromDto(userUpdateDto, existingUser);

        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isEmpty()) {
            // Get and encode user password
            existingUser.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.userToUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}
