package com.example.demo.service.k1;

import com.example.demo.dto.request.StudentProgressRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.StudentProgressResponse;

public interface StudentProgressService {
    StudentProgressResponse getStudentProgress(Long studentId, Long courseClassId);

    PageResponse<StudentProgressResponse> getListStudentProgress(int page, int size);

    StudentProgressResponse addStudentProgress(StudentProgressRequest request);

    String deleteStudentProgress(Long studentId, Long courseClassId);
}
