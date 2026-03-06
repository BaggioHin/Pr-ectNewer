package com.example.demo.controller;

import com.example.demo.dto.request.ClassSessionRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ClassSessionResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.k1.ClassSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/class-sessions")
public class ClassSessionController {
    @Autowired
    ClassSessionService classSessionService;

    @GetMapping("/{id}")
    ApiResponse<ClassSessionResponse> getClassSessionById(@PathVariable Long id) {
        return ApiResponse.<ClassSessionResponse>builder()
                .result(classSessionService.getClassSessionById(id))
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<ClassSessionResponse>> getListClassSession(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ClassSessionResponse>>builder()
                .result(classSessionService.getListClassSession(page, size))
                .build();
    }

    @PostMapping
    ApiResponse<ClassSessionResponse> addClassSession(@RequestBody ClassSessionRequest request) {
        return ApiResponse.<ClassSessionResponse>builder()
                .result(classSessionService.addClassSession(request))
                .build();
    }

    @PostMapping("/makeup")
    ApiResponse<ClassSessionResponse> addMakeupSession(@RequestBody ClassSessionRequest request) {
        return ApiResponse.<ClassSessionResponse>builder()
                .result(classSessionService.addMakeupSession(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ClassSessionResponse> editClassSession(
            @PathVariable Long id,
            @RequestBody ClassSessionRequest request) {
        return ApiResponse.<ClassSessionResponse>builder()
                .result(classSessionService.editClassSession(id, request))
                .build();
    }

    @PutMapping("/makeup/{id}")
    ApiResponse<ClassSessionResponse> editMakeupSession(
            @PathVariable Long id,
            @RequestBody ClassSessionRequest request) {
        return ApiResponse.<ClassSessionResponse>builder()
                .result(classSessionService.editMakeupSession(id, request))
                .build();
    }

    @GetMapping("/makeup/{id}")
    ApiResponse<ClassSessionResponse> getMakeupSessionById(@PathVariable Long id) {
        return ApiResponse.<ClassSessionResponse>builder()
                .result(classSessionService.getMakeupSessionById(id))
                .build();
    }

    @GetMapping("/makeup/by-course-class/{courseClassId}")
    ApiResponse<java.util.List<ClassSessionResponse>> getMakeupSessionsByCourseClass(@PathVariable Long courseClassId) {
        return ApiResponse.<java.util.List<ClassSessionResponse>>builder()
                .result(classSessionService.getMakeupSessionsByCourseClass(courseClassId))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteClassSession(@PathVariable Long id) {
        return ApiResponse.<String>builder()
                .result(classSessionService.deleteClassSession(id))
                .build();
    }
}
