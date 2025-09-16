package com.parentportal.payment_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// This DTO mirrors a simplified StudentDto from the Student Performance Service
// It's used to receive student data when calling the Student Performance Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {

    private Long id;
    private String studentId;
    private Long parentUserId;
}
