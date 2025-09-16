package com.parentportal.student_performance_service.dto;

import com.parentportal.student_performance_service.entity.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {

    private Long id;
    private LocalDate date;
    private AttendanceStatus status; // Use the enum
    private String reason;
    private Long studentId; // To link back to the student
}
