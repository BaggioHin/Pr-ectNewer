package com.example.demo.controller;

import com.example.demo.dto.request.ExamQuestionRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ExamQuestionResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.k1.ExamQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam-questions")
public class ExamQuestionController {

    @Autowired
    private ExamQuestionService examQuestionService;

    @GetMapping("/{id}")
    ApiResponse<ExamQuestionResponse> getExamQuestionById(@PathVariable Long id) {
        return ApiResponse.<ExamQuestionResponse>builder()
                .result(examQuestionService.getExamQuestionById(id))
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<ExamQuestionResponse>> getListExamQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long examId) {
        return ApiResponse.<PageResponse<ExamQuestionResponse>>builder()
                .result(examQuestionService.getListExamQuestions(page, size, examId))
                .build();
    }

    @GetMapping("/review")
    ApiResponse<java.util.List<ExamQuestionResponse>> getReviewQuestions(
            @RequestParam Long examId) {
        return ApiResponse.<java.util.List<ExamQuestionResponse>>builder()
                .result(examQuestionService.getReviewQuestions(examId))
                .build();
    }

    @PostMapping
    ApiResponse<ExamQuestionResponse> createExamQuestion(@RequestBody ExamQuestionRequest request) {
        return ApiResponse.<ExamQuestionResponse>builder()
                .result(examQuestionService.createExamQuestion(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ExamQuestionResponse> editExamQuestion(@PathVariable Long id,
                                                       @RequestBody ExamQuestionRequest request) {
        return ApiResponse.<ExamQuestionResponse>builder()
                .result(examQuestionService.editExamQuestion(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteExamQuestion(@PathVariable Long id) {
        return ApiResponse.<String>builder()
                .result(examQuestionService.deleteExamQuestion(id))
                .build();
    }

    @GetMapping("/count")
    ApiResponse<Long> countExamQuestions(@RequestParam(required = false) Long examId) {
        return ApiResponse.<Long>builder()
                .result(examQuestionService.countExamQuestions(examId))
                .build();
    }
}
