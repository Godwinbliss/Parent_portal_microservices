package com.parentportal.student_performance_service.service.impl;

import com.parentportal.student_performance_service.dto.*;
import com.parentportal.student_performance_service.dto.user.Roles;
import com.parentportal.student_performance_service.dto.user.UserDto;
import com.parentportal.student_performance_service.entity.Attendance;
import com.parentportal.student_performance_service.entity.Result;
import com.parentportal.student_performance_service.entity.Student;
import com.parentportal.student_performance_service.mapper.AttendanceMapper;
import com.parentportal.student_performance_service.mapper.ResultMapper;
import com.parentportal.student_performance_service.mapper.StudentMapper;
import com.parentportal.student_performance_service.repository.AttendanceRepository;
import com.parentportal.student_performance_service.repository.ResultRepository;
import com.parentportal.student_performance_service.repository.StudentRepository;
import com.parentportal.student_performance_service.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ResultRepository resultRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentMapper studentMapper;
    private final ResultMapper resultMapper;
    private final AttendanceMapper attendanceMapper;
    private final WebClient.Builder webClient; // NEWLY ADDED

    @Value("${user-management-service.url}")
    private String userManagementServiceUrl;

    private Mono<UserDto> getAdminUser(Long adminUserId) {
        return webClient.build().get()
                .uri("lb://" + userManagementServiceUrl
                        + "/api/users/{id}", adminUserId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new NoSuchElementException("Admin user not found with ID: " + adminUserId)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("User service error: " + clientResponse.statusCode())))
                .bodyToMono(UserDto.class)
                .filter(user -> Roles.ADMIN == user.getRole())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User with ID " + adminUserId + " is not an ADMIN.")));
    }

    private Mono<UserDto> getParentUser(Long parentUserId) {
        return webClient.build().get()
                .uri("lb://" + userManagementServiceUrl
                        + "/api/users/{id}", parentUserId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new NoSuchElementException("Parent user not found with ID: " + parentUserId)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("User service error: " + clientResponse.statusCode())))
                .bodyToMono(UserDto.class)
                .filter(user -> Roles.PARENT == user.getRole())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User with ID " + parentUserId + " is not a PARENT.")));
    }

    @Override
    @Transactional
    public StudentDto createStudent(Long adminUserId, StudentCreateDto studentCreateDto) {
        getAdminUser(adminUserId).block();
        getParentUser(studentCreateDto.getParentUserId()).block();
        Student student = studentMapper.studentCreateDtoToStudent(studentCreateDto);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.studentToStudentDto(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Student not found with ID: " + id));
        return studentMapper.studentToStudentDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return studentMapper.studentListToStudentDtoList(students);
    }

    @Override
    @Transactional
    public StudentDto updateStudent(Long adminUserId, Long id, StudentUpdateDto studentUpdateDto) {
        getAdminUser(adminUserId).block();
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Student not found with ID: " + id));
        studentMapper.updateStudentFromDto(studentUpdateDto, existingStudent);
        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.studentToStudentDto(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(Long adminUserId, Long id) {
        getAdminUser(adminUserId).block();
        if (!studentRepository.existsById(id)) {
            throw new NoSuchElementException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsByParentUserId(Long parentUserId) {
        List<Student> students = studentRepository.findByParentUserId(parentUserId);
        return studentMapper.studentListToStudentDtoList(students);
    }

    @Override
    @Transactional
    public ResultDto addResultToStudent(Long adminUserId, Long studentId, ResultCreateDto resultCreateDto) {
        getAdminUser(adminUserId).block();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found with ID: " + studentId));
        Result result = resultMapper.resultCreateDtoToResult(resultCreateDto);
        result.setStudent(student);
        Result savedResult = resultRepository.save(result);
        return resultMapper.resultToResultDto(savedResult);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultDto> getResultsByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new NoSuchElementException("Student not found with ID: " + studentId);
        }
        List<Result> results = resultRepository.findByStudentId(studentId);
        return resultMapper.resultListToResultDtoList(results);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultDto getResultById(Long resultId) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new NoSuchElementException("Result not found with ID: " + resultId));
        return resultMapper.resultToResultDto(result);
    }

    @Override
    @Transactional
    public void deleteResult(Long adminUserId, Long resultId) {
        getAdminUser(adminUserId).block();
        if (!resultRepository.existsById(resultId)) {
            throw new NoSuchElementException("Result not found with ID: " + resultId);
        }
        resultRepository.deleteById(resultId);
    }

    @Override
    @Transactional
    public AttendanceDto addAttendanceToStudent(Long adminUserId, Long studentId, AttendanceCreateDto attendanceCreateDto) {
        getAdminUser(adminUserId).block();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found with ID: " + studentId));
        Attendance attendance = attendanceMapper.attendanceCreateDtoToAttendance(attendanceCreateDto);
        attendance.setStudent(student);
        Attendance savedAttendance = attendanceRepository.save(attendance);
        return attendanceMapper.attendanceToAttendanceDto(savedAttendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new NoSuchElementException("Student not found with ID: " + studentId);
        }
        List<Attendance> attendanceRecords = attendanceRepository.findByStudentId(studentId);
        return attendanceMapper.attendanceListToAttendanceDtoList(attendanceRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDto getAttendanceById(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new NoSuchElementException("Attendance record not found with ID: " + attendanceId));
        return attendanceMapper.attendanceToAttendanceDto(attendance);
    }

    @Override
    @Transactional
    public void deleteAttendance(Long adminUserId, Long attendanceId) {
        getAdminUser(adminUserId).block();
        if (!attendanceRepository.existsById(attendanceId)) {
            throw new NoSuchElementException("Attendance record not found with ID: " + attendanceId);
        }
        attendanceRepository.deleteById(attendanceId);
    }
}
