package com.example.demo.service.k1;

import com.example.demo.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {
    List<StudentResponse> getStudentByCourseClass(Long courseClassId);
}
