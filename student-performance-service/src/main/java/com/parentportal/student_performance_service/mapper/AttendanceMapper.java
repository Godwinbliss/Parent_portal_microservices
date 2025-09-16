package com.parentportal.student_performance_service.mapper;

import com.parentportal.student_performance_service.dto.AttendanceCreateDto;
import com.parentportal.student_performance_service.dto.AttendanceDto;
import com.parentportal.student_performance_service.entity.Attendance;
import com.parentportal.student_performance_service.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    AttendanceMapper INSTANCE = Mappers.getMapper(AttendanceMapper.class);

    @Mapping(source = "student.id", target = "studentId")
    AttendanceDto attendanceToAttendanceDto(Attendance attendance);

    @Mapping(target = "student", source = "studentId", qualifiedByName = "mapStudentIdToStudent")
    Attendance attendanceCreateDtoToAttendance(AttendanceCreateDto attendanceCreateDto);

    List<AttendanceDto> attendanceListToAttendanceDtoList(List<Attendance> attendanceRecords);

    // Helper method to map studentId to Student entity
    @Named("mapStudentIdToStudent")
    default Student mapStudentIdToStudent(Long studentId) {
        if (studentId == null) {
            return null;
        }
        // In a real application, you would fetch the Student entity from the database
        // For now, return a new Student with just the ID
        Student student = new Student();
        student.setId(studentId);
        return student;
    }
}
