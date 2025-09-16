package com.parentportal.student_performance_service.mapper;


import com.parentportal.student_performance_service.dto.ResultCreateDto;
import com.parentportal.student_performance_service.dto.ResultDto;
import com.parentportal.student_performance_service.entity.Result;
import com.parentportal.student_performance_service.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResultMapper {

    ResultMapper INSTANCE = Mappers.getMapper(ResultMapper.class);

    @Mapping(source = "student.id", target = "studentId")
    ResultDto resultToResultDto(Result result);

    @Mapping(target = "student", source = "studentId", qualifiedByName = "mapStudentIdToStudent")
    Result resultCreateDtoToResult(ResultCreateDto resultCreateDto);

    List<ResultDto> resultListToResultDtoList(List<Result> results);

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
