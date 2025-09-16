package com.parent.portal.user_management_service.mapper;

import com.parent.portal.user_management_service.dto.UserCreateDto;
import com.parent.portal.user_management_service.dto.UserDto;
import com.parent.portal.user_management_service.dto.UserUpdateDto;
import com.parent.portal.user_management_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring") // Makes MapStruct generate a Spring component
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Map User entity to UserDto (excluding password)
    UserDto userToUserDto(User user);

    // Map UserCreateDto to User entity
    User userCreateDtoToUser(UserCreateDto userCreateDto);

    // Map UserUpdateDto to existing User entity, ignoring null fields in DTO
    @Mapping(target = "id", ignore = true) // ID should not be updated from DTO
    @Mapping(target = "password", ignore = true) // Password is handled separately for security
    void updateUserFromDto(UserUpdateDto userUpdateDto, @MappingTarget User user);

    // Map list of User entities to list of UserDtos
    List<UserDto> userListToUserDtoList(List<User> users);
}
