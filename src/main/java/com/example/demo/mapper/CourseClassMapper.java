package com.example.demo.mapper;

import com.example.demo.dto.request.CourseClassRequest;
import com.example.demo.dto.response.CourseClassResponse;
import com.example.demo.dto.response.CourseClassSummary;
import com.example.demo.entity.classAndLearn.CourseClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseClassMapper {
    @Mapping(source = "classCode", target = "code")
    @Mapping(source = "startDay", target = "startDate")
    @Mapping(source = "endDay", target = "endDate")
    CourseClassResponse entityToResponse(CourseClass courseClass);

    List<CourseClassResponse> entityToListResponse(List<CourseClass> courseClasses);

    @Mapping(source = "code", target = "classCode")
    @Mapping(source = "startDate", target = "startDay")
    @Mapping(source = "endDate", target = "endDay")
    void requestToEntity(CourseClassRequest courseClassRequest, @MappingTarget CourseClass courseClass);

    CourseClassSummary entityToSummary(CourseClass courseClass);
}
