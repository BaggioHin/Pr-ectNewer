package com.example.demo.service.k1;

import com.example.demo.dto.request.ExamQuestionRequest;
import com.example.demo.dto.response.ExamQuestionResponse;
import com.example.demo.dto.response.PageResponse;

import java.util.List;

public interface ExamQuestionService {
    ExamQuestionResponse getExamQuestionById(Long id);

    PageResponse<ExamQuestionResponse> getListExamQuestions(int page, int size, Long examId);

    List<ExamQuestionResponse> getReviewQuestions(Long examId);

    ExamQuestionResponse createExamQuestion(ExamQuestionRequest request);

    ExamQuestionResponse editExamQuestion(Long id, ExamQuestionRequest request);

    String deleteExamQuestion(Long id);

    long countExamQuestions(Long examId);
}
