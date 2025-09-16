package com.parentportal.student_performance_service.mapper;

import com.parentportal.student_performance_service.dto.StudentCreateDto;
import com.parentportal.student_performance_service.dto.StudentDto;
import com.parentportal.student_performance_service.dto.StudentUpdateDto;
import com.parentportal.student_performance_service.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ResultMapper.class, AttendanceMapper.class}) // Include other mappers
public interface StudentMapper {

    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    StudentDto studentToStudentDto(Student student);

    @Mapping(target = "results", ignore = true) // Ignore nested collections for creation
    @Mapping(target = "attendanceRecords", ignore = true)
    Student studentCreateDtoToStudent(StudentCreateDto studentCreateDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "results", ignore = true)
    @Mapping(target = "attendanceRecords", ignore = true)
    void updateStudentFromDto(StudentUpdateDto studentUpdateDto, @MappingTarget Student student);

    List<StudentDto> studentListToStudentDtoList(List<Student> students);
}
