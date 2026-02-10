package com.example.demo.service.k1;

import com.example.demo.dto.response.TeacherResponse;

import java.util.List;

public interface TeacherService {
    List<TeacherResponse> getTeacherByCourseClass(Long courseClassId);
}
