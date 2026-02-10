package com.example.demo.controller;

import com.example.demo.dto.request.StudentProgressRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.StudentProgressResponse;
import com.example.demo.service.k1.StudentProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student-progress")
public class StudentProgressController {
    @Autowired
    StudentProgressService studentProgressService;

    @GetMapping("/{studentId}/{courseClassId}")
    ApiResponse<StudentProgressResponse> getStudentProgress(@PathVariable Long studentId,
                                                            @PathVariable Long courseClassId) {
        return ApiResponse.<StudentProgressResponse>builder()
                .result(studentProgressService.getStudentProgress(studentId, courseClassId))
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<StudentProgressResponse>> getListStudentProgress(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<StudentProgressResponse>>builder()
                .result(studentProgressService.getListStudentProgress(page, size))
                .build();
    }

    @PostMapping
    ApiResponse<StudentProgressResponse> addStudentProgress(@RequestBody StudentProgressRequest request) {
        return ApiResponse.<StudentProgressResponse>builder()
                .result(studentProgressService.addStudentProgress(request))
                .build();
    }

    @DeleteMapping("/{studentId}/{courseClassId}")
    ApiResponse<String> deleteStudentProgress(@PathVariable Long studentId,
                                              @PathVariable Long courseClassId) {
        return ApiResponse.<String>builder()
                .result(studentProgressService.deleteStudentProgress(studentId, courseClassId))
                .build();
    }
}
