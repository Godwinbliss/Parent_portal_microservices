package com.parentportal.student_performance_service.service;

import com.parentportal.student_performance_service.dto.*;

import java.util.List;

public interface StudentService {
    // Student operations
    StudentDto createStudent(Long adminUserId, StudentCreateDto studentCreateDto);
    StudentDto getStudentById(Long id);
    List<StudentDto> getAllStudents();
    StudentDto updateStudent(Long adminUserId, Long id, StudentUpdateDto studentUpdateDto);
    void deleteStudent(Long adminUserId, Long id);
    List<StudentDto> getStudentsByParentUserId(Long parentUserId);

    ResultDto addResultToStudent(Long adminUserId, Long studentId, ResultCreateDto resultCreateDto);
    List<ResultDto> getResultsByStudentId(Long studentId);
    ResultDto getResultById(Long resultId);
    void deleteResult(Long adminUserId, Long resultId);

    AttendanceDto addAttendanceToStudent(Long adminUserId, Long studentId, AttendanceCreateDto attendanceCreateDto);
    List<AttendanceDto> getAttendanceByStudentId(Long studentId);
    AttendanceDto getAttendanceById(Long attendanceId);
    void deleteAttendance(Long adminUserId, Long attendanceId);
}
