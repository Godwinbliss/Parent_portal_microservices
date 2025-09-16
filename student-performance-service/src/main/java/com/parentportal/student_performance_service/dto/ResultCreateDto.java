package com.parentportal.student_performance_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultCreateDto {

    @NotBlank(message = "Subject is required")
    private String subject;
    @NotBlank(message = "Grade is required")
    private String grade;
    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score cannot be negative")
    private Double score;
    @NotNull(message = "Date is required")
    @JsonFormat(pattern = "MM-dd-yyyy")  // Use @JsonFormat instead of @DateTimeFormat
    private LocalDate date;
    @NotNull(message = "Student ID is required")
    private Long studentId;
}
