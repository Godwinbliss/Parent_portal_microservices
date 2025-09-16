package com.parentportal.student_performance_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String studentId;
    private Long parentUserId;
    private List<ResultDto> results; // Include DTOs for nested relationships
    private List<AttendanceDto> attendanceRecords;
}
