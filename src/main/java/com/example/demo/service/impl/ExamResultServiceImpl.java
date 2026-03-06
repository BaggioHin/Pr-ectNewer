package com.example.demo.service.impl;

import com.example.demo.dto.response.ExamResultResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.gradeAndEvaluate.Exam;
import com.example.demo.entity.gradeAndEvaluate.ExamResult;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.ExamResultRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.k1.ExamResultService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExamResultServiceImpl implements ExamResultService {

    @Autowired
    private ExamResultRepository examResultRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('STUDENT')")
    public PageResponse<ExamResultResponse> getMyResults(Long examId, int page, int size) {
        Long studentId = resolveUserId();
        if (!studentRepository.existsById(studentId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ExamResult> pageResult;
        if (examId != null) {
            if (!examRepository.existsById(examId)) {
                throw new AppException(ErrorCode.IMFORMATION_NULL);
            }
            pageResult = examResultRepository.findByStudent_UserIdAndExam_Id(studentId, examId, pageable);
        } else {
            pageResult = examResultRepository.findByStudent_UserId(studentId, pageable);
        }

        List<ExamResultResponse> data = pageResult.getContent()
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

    private ExamResultResponse toResponse(ExamResult result) {
        Exam exam = result.getExam();
        return ExamResultResponse.builder()
                .id(result.getId())
                .examId(exam != null ? exam.getId() : null)
                .studentId(result.getStudent() != null ? result.getStudent().getUserId() : null)
                .typeGrade(exam != null ? exam.getTypeGrade() : null)
                .score(result.getScore())
                .examDate(exam != null ? exam.getExamDate() : null)
                .duration(exam != null ? exam.getDuration() : 0)
                .subjectId(exam != null && exam.getSubject() != null ? exam.getSubject().getId() : null)
                .subjectName(exam != null && exam.getSubject() != null ? exam.getSubject().getName() : null)
                .courseClassId(exam != null && exam.getCourseClass() != null ? exam.getCourseClass().getId() : null)
                .courseClassName(exam != null && exam.getCourseClass() != null ? exam.getCourseClass().getName() : null)
                .courseId(exam != null && exam.getCourseClass() != null && exam.getCourseClass().getCourse() != null
                        ? exam.getCourseClass().getCourse().getId()
                        : null)
                .courseName(exam != null && exam.getCourseClass() != null && exam.getCourseClass().getCourse() != null
                        ? exam.getCourseClass().getCourse().getName()
                        : null)
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
