package com.example.demo.service.k1;

import com.example.demo.dto.request.CourseClassRequest;
import com.example.demo.dto.response.CourseClassResponse;
import com.example.demo.dto.response.PageResponse;

import java.util.List;

public interface CourseClassService {
    CourseClassResponse getCourseClassById(Long id);

    List<CourseClassResponse> getCourseClassByName(String name);

    PageResponse<CourseClassResponse> getListCourseClass(int page,int size);

    CourseClassResponse editCourseClass(Long id,CourseClassRequest courseClassRequest);

    CourseClassResponse addCourseClass(CourseClassRequest courseClassRequest);

    String deleteCourseClass(Long id);
}
