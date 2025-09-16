package com.parentportal.student_performance_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {

    private Long id;
    private String subject;
    private String grade;
    private Double score;
    private LocalDate date;
    private Long studentId; // To link back to the student
}
