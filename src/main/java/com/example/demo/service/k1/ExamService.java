package com.example.demo.service.k1;

import com.example.demo.dto.request.ExamRequest;
import com.example.demo.dto.request.ExamSubmitRequest;
import com.example.demo.dto.response.ExamResponse;
import com.example.demo.dto.response.ExamSubmitResponse;
import com.example.demo.dto.response.PageResponse;

import java.util.List;

public interface ExamService {

    ExamResponse createExam(ExamRequest request);

    String editExamInCourseClass(Long id, Long examId, ExamRequest request);

    String deleteExamInCourseClass(Long id, Long examId);

    ExamResponse getExamById(Long id);

    PageResponse<ExamResponse> getListExam(int page, int size);

    List<ExamResponse> getExamByCourseClassName(String name);

    ExamSubmitResponse submitExam(Long examId, ExamSubmitRequest request);
}
