package com.example.demo.service.k1;

import com.example.demo.dto.response.ClassProgressResponse;
import com.example.demo.dto.response.StudentClassProgressResponse;

public interface ProgressService {
    ClassProgressResponse getClassProgress(Long courseClassId);

    StudentClassProgressResponse getMyClassProgress(Long courseClassId);
}
