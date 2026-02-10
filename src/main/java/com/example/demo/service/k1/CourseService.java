package com.example.demo.service.k1;

import com.example.demo.dto.request.CourseRequest;
import com.example.demo.dto.response.CourseResponse;
import com.example.demo.dto.response.PageResponse;

public interface CourseService {
    CourseResponse getCourseById(Long id);

    CourseResponse getCourseByName(String name);

    PageResponse<CourseResponse> getListCourse(int page, int size);

    CourseResponse editCourse(Long id, CourseRequest courseRequest);

    CourseResponse addCourse(CourseRequest courseRequest);

    String deleteCourse(Long id);

    String addCourseClass(Long courseId, Long classId);
    String deleteCourseClass(Long courseId, Long classId);
    String addSubject(Long courseId, Long subjectId);
    String deleteSubject(Long courseId, Long subjectId);
}
