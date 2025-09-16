package com.parentportal.student_performance_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.parentportal.student_performance_service.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCreateDto {

    @NotNull(message = "Date is required")
    @JsonFormat(pattern = "MM-dd-yyyy")  // Use @JsonFormat instead of @DateTimeFormat
    private LocalDate date;
    @NotNull(message = "Status is required") // Changed from @NotBlank to @NotNull for enum
    private AttendanceStatus status; // Use the enum
    private String reason; // Optional
    @NotNull(message = "Student ID is required")
    private Long studentId;
}
