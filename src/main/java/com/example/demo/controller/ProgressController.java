package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ClassProgressResponse;
import com.example.demo.dto.response.StudentClassProgressResponse;
import com.example.demo.service.k1.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @GetMapping("/class/{courseClassId}")
    ApiResponse<ClassProgressResponse> getClassProgress(@PathVariable Long courseClassId) {
        return ApiResponse.<ClassProgressResponse>builder()
                .result(progressService.getClassProgress(courseClassId))
                .build();
    }

    @GetMapping("/class/{courseClassId}/me")
    ApiResponse<StudentClassProgressResponse> getMyClassProgress(@PathVariable Long courseClassId) {
        return ApiResponse.<StudentClassProgressResponse>builder()
                .result(progressService.getMyClassProgress(courseClassId))
                .build();
    }
}
