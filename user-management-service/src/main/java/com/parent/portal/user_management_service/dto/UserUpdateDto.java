package com.parent.portal.user_management_service.dto;

import com.parent.portal.user_management_service.entity.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters long if provided")
    private String password; // Optional for update

    @Email(message = "Email should be valid")
    private String email;

    private Roles role;
}
