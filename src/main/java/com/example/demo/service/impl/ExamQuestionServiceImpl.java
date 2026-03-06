package com.example.demo.service.impl;

import com.example.demo.dto.request.ExamQuestionRequest;
import com.example.demo.dto.response.ExamQuestionResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.gradeAndEvaluate.Exam;
import com.example.demo.entity.gradeAndEvaluate.ExamQuestion;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.ExamQuestionRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.ExamResultRepository;
import com.example.demo.service.k1.ExamQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamQuestionServiceImpl implements ExamQuestionService {

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamResultRepository examResultRepository;

    @Override
    public ExamQuestionResponse getExamQuestionById(Long id) {
        ExamQuestion question = examQuestionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(question);
    }

    @Override
    public PageResponse<ExamQuestionResponse> getListExamQuestions(int page, int size, Long examId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ExamQuestion> pageResult;
        if (examId != null) {
            if (!examRepository.existsById(examId)) {
                throw new AppException(ErrorCode.IMFORMATION_NULL);
            }
            pageResult = examQuestionRepository.findByExam_Id(examId, pageable);
        } else {
            pageResult = examQuestionRepository.findAll(pageable);
        }

        List<ExamQuestionResponse> data = pageResult.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public List<ExamQuestionResponse> getReviewQuestions(Long examId) {
        if (examId == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        if (!examRepository.existsById(examId)) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }

        Long studentId = resolveUserId();
        boolean done = examResultRepository.existsByExam_IdAndStudent_UserId(examId, studentId);
        if (!done) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }

        return examQuestionRepository.findByExam_Id(examId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ExamQuestionResponse createExamQuestion(ExamQuestionRequest request) {
        if (request == null || request.getExamId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        ExamQuestion question = new ExamQuestion();
        question.setExam(exam);
        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectOption(request.getCorrectOption());
        question.setExplanation(request.getExplanation());

        ExamQuestion saved = examQuestionRepository.save(question);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ExamQuestionResponse editExamQuestion(Long id, ExamQuestionRequest request) {
        ExamQuestion question = examQuestionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        if (request != null) {
            if (request.getExamId() != null) {
                Exam exam = examRepository.findById(request.getExamId())
                        .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
                question.setExam(exam);
            }
            if (request.getQuestionText() != null) {
                question.setQuestionText(request.getQuestionText());
            }
            if (request.getOptionA() != null) {
                question.setOptionA(request.getOptionA());
            }
            if (request.getOptionB() != null) {
                question.setOptionB(request.getOptionB());
            }
            if (request.getOptionC() != null) {
                question.setOptionC(request.getOptionC());
            }
            if (request.getOptionD() != null) {
                question.setOptionD(request.getOptionD());
            }
            if (request.getCorrectOption() != null) {
                question.setCorrectOption(request.getCorrectOption());
            }
            if (request.getExplanation() != null) {
                question.setExplanation(request.getExplanation());
            }
        }

        ExamQuestion saved = examQuestionRepository.save(question);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteExamQuestion(Long id) {
        ExamQuestion question = examQuestionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        examQuestionRepository.delete(question);
        return "Delete successful!";
    }

    @Override
    public long countExamQuestions(Long examId) {
        if (examId == null) {
            return examQuestionRepository.count();
        }
        if (!examRepository.existsById(examId)) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        return examQuestionRepository.countByExam_Id(examId);
    }

    private ExamQuestionResponse toResponse(ExamQuestion question) {
        return ExamQuestionResponse.builder()
                .id(question.getId())
                .examId(question.getExam() != null ? question.getExam().getId() : null)
                .questionText(question.getQuestionText())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .correctOption(question.getCorrectOption())
                .explanation(question.getExplanation())
                .build();
    }

    private Long resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return jwt.getClaim("userId");
    }
}
