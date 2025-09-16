package com.parentportal.student_performance_service.controller;

import com.parentportal.student_performance_service.dto.*;
import com.parentportal.student_performance_service.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Student Endpoints
    /**
     * Creates a new student record. Accessible only by ADMINs.
     * @param adminUserId The ID of the authenticated admin.
     * @param studentCreateDto DTO containing student creation details.
     * @return The created student as a DTO with HTTP status 201 (Created).
     */
    @PostMapping("/{adminUserId}")
    public ResponseEntity<StudentDto> createStudent(@PathVariable Long adminUserId, @Valid @RequestBody StudentCreateDto studentCreateDto) {
        try {
            StudentDto createdStudent = studentService.createStudent(adminUserId, studentCreateDto);
            return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Retrieves a student by their ID.
     * @param id The ID of the student.
     * @return The student as a DTO if found, or HTTP status 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        try {
            StudentDto student = studentService.getStudentById(id);
            return new ResponseEntity<>(student, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all student records.
     * @return A list of all students as DTOs.
     */
    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        List<StudentDto> students = studentService.getAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * Retrieves students associated with a specific parent user ID.
     * @param parentUserId The ID of the parent user.
     * @return A list of students as DTOs associated with the parent.
     */
    @GetMapping("/byParent/{parentUserId}")
    public ResponseEntity<List<StudentDto>> getStudentsByParentUserId(@PathVariable Long parentUserId) {
        List<StudentDto> students = studentService.getStudentsByParentUserId(parentUserId);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * Updates an existing student record. Accessible only by ADMINs.
     * @param adminUserId The ID of the authenticated admin.
     * @param id The ID of the student to update.
     * @param studentUpdateDto DTO containing updated student details.
     * @return The updated student as a DTO if found, or HTTP status 404 (Not Found).
     */
    @PutMapping("/{adminUserId}/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long adminUserId, @PathVariable Long id, @Valid @RequestBody StudentUpdateDto studentUpdateDto) {
        try {
            StudentDto updatedStudent = studentService.updateStudent(adminUserId, id, studentUpdateDto);
            return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Deletes a student record by ID. Accessible only by ADMINs.
     * @param adminUserId The ID of the authenticated admin.
     * @param id The ID of the student to delete.
     * @return HTTP status 204 (No Content) on successful deletion, or 404 (Not Found) if student does not exist.
     */
    @DeleteMapping("/{adminUserId}/{id}")
    public ResponseEntity<HttpStatus> deleteStudent(@PathVariable Long adminUserId, @PathVariable Long id) {
        try {
            studentService.deleteStudent(adminUserId, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Result Endpoints
    /**
     * Adds a new result for a specific student. Accessible only by ADMINs.
     * @param adminUserId The ID of the authenticated admin.
     * @param studentId The ID of the student to add the result to.
     * @param resultCreateDto DTO containing result creation details.
     * @return The created result as a DTO with HTTP status 201 (Created).
     */
    @PostMapping("/{adminUserId}/{studentId}/results")
    public ResponseEntity<ResultDto> addResultToStudent(@PathVariable Long adminUserId, @PathVariable Long studentId, @Valid @RequestBody ResultCreateDto resultCreateDto) {
        try {
            ResultDto createdResult = studentService.addResultToStudent(adminUserId, studentId, resultCreateDto);
            return new ResponseEntity<>(createdResult, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Retrieves all results for a specific student.
     * @param studentId The ID of the student.
     * @return A list of result DTOs for the specified student.
     */
    @GetMapping("/{studentId}/results")
    public ResponseEntity<List<ResultDto>> getResultsByStudentId(@PathVariable Long studentId) {
        try {
            List<ResultDto> results = studentService.getResultsByStudentId(studentId);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves a specific result by its ID.
     * @param resultId The ID of the result.
     * @return The result as a DTO if found, or HTTP status 404 (Not Found).
     */
    @GetMapping("/results/{resultId}")
    public ResponseEntity<ResultDto> getResultById(@PathVariable Long resultId) {
        try {
            ResultDto result = studentService.getResultById(resultId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a specific result by its ID. Accessible only by ADMINs.
     * @param adminUserId The ID of the authenticated admin.
     * @param resultId The ID of the result to delete.
     * @return HTTP status 204 (No Content) on successful deletion, or 404 (Not Found).
     */
    @DeleteMapping("/{adminUserId}/results/{resultId}")
    public ResponseEntity<HttpStatus> deleteResult(@PathVariable Long adminUserId, @PathVariable Long resultId) {
        try {
            studentService.deleteResult(adminUserId, resultId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Attendance Endpoints
    /**
     * Adds a new attendance record for a specific student. Accessible only by ADMINs.
     * @param adminUserId The ID of the authenticated admin.
     * @param studentId The ID of the student to add the attendance to.
     * @param attendanceCreateDto DTO containing attendance creation details.
     * @return The created attendance record as a DTO with HTTP status 201 (Created).
     */
    @PostMapping("/{adminUserId}/{studentId}/attendance")
    public ResponseEntity<AttendanceDto> addAttendanceToStudent(@PathVariable Long adminUserId, @PathVariable Long studentId, @Valid @RequestBody AttendanceCreateDto attendanceCreateDto) {
        try {
            AttendanceDto createdAttendance = studentService.addAttendanceToStudent(adminUserId, studentId, attendanceCreateDto);
            return new ResponseEntity<>(createdAttendance, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Retrieves all attendance records for a specific student.
     * @param studentId The ID of the student.
     * @return A list of attendance DTOs for the specified student.
     */
    @GetMapping("/{studentId}/attendance")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByStudentId(@PathVariable Long studentId) {
        try {
            List<AttendanceDto> attendanceRecords = studentService.getAttendanceByStudentId(studentId);
            return new ResponseEntity<>(attendanceRecords, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves a specific attendance record by its ID.
     * @param attendanceId The ID of the attendance record.
     * @return The attendance record as a DTO if found, or HTTP status 404 (Not Found).
     */
    @GetMapping("/attendance/{attendanceId}")
    public ResponseEntity<AttendanceDto> getAttendanceById(@PathVariable Long attendanceId) {
        try {
            AttendanceDto attendance = studentService.getAttendanceById(attendanceId);
            return new ResponseEntity<>(attendance, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a specific attendance record by its ID. Accessible only by ADMINs.
     * @param adminUserId The ID of the authenticated admin.
     * @param attendanceId The ID of the attendance record to delete.
     * @return HTTP status 204 (No Content) on successful deletion, or 404 (Not Found).
     */
    @DeleteMapping("/{adminUserId}/attendance/{attendanceId}")
    public ResponseEntity<HttpStatus> deleteAttendance(@PathVariable Long adminUserId, @PathVariable Long attendanceId) {
        try {
            studentService.deleteAttendance(adminUserId, attendanceId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
