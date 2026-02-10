package com.example.demo.controller;

import com.example.demo.dto.request.ExamRequest;
import com.example.demo.dto.request.ExamSubmitRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ExamResponse;
import com.example.demo.dto.response.ExamSubmitResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.k1.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamController {
    @Autowired
    ExamService examService;

    @Operation(summary = "Get exam by Id")
    @GetMapping("/{id}")
    ApiResponse<ExamResponse> getExamById(@PathVariable Long id) {
        return ApiResponse.<ExamResponse>builder()
                .result(examService.getExamById(id))
                .build();
    }

    @Operation(summary = "Get list exam")
    @GetMapping
    ApiResponse<PageResponse<ExamResponse>> getListExam(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ExamResponse>>builder()
                .result(examService.getListExam(page, size))
                .build();
    }

    @Operation(summary = "Get exam by course class name")
    @GetMapping("/search")
    ApiResponse<List<ExamResponse>> getExamByCourseClassName(@RequestParam String name) {
        return ApiResponse.<List<ExamResponse>>builder()
                .result(examService.getExamByCourseClassName(name))
                .build();
    }

    @Operation(summary = "Create new exam")
    @PostMapping
    ApiResponse<ExamResponse> createExam(@RequestBody ExamRequest request) {
        return ApiResponse.<ExamResponse>builder()
                .result(examService.createExam(request))
                .build();
    }

    @Operation(summary = "Edit exam in course class")
    @PutMapping("/{id}/exams/{examId}")
    ApiResponse<String> editExamInCourseClass(@PathVariable Long id,
                                              @PathVariable Long examId,
                                              @RequestBody ExamRequest examRequest) {
        return ApiResponse.<String>builder()
                .result(examService.editExamInCourseClass(id, examId, examRequest))
                .build();
    }

    @Operation(summary = "Delete exam from course class")
    @DeleteMapping("/{id}/exams/{examId}")
    ApiResponse<String> deleteExamInCourseClass(@PathVariable Long id, @PathVariable Long examId) {
        return ApiResponse.<String>builder()
                .result(examService.deleteExamInCourseClass(id, examId))
                .build();
    }

    @Operation(summary = "Submit exam")
    @PostMapping("/{examId}/submit")
    ApiResponse<ExamSubmitResponse> submitExam(
            @PathVariable Long examId,
            @RequestBody ExamSubmitRequest request) {
        return ApiResponse.<ExamSubmitResponse>builder()
                .result(examService.submitExam(examId, request))
                .build();
    }
}
