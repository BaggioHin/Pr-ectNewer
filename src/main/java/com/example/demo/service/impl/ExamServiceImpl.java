package com.example.demo.service.impl;

import com.example.demo.constant.TypeGrade;
import com.example.demo.dto.request.ExamRequest;
import com.example.demo.dto.request.ExamSubmitRequest;
import com.example.demo.dto.response.ExamResponse;
import com.example.demo.dto.response.ExamSubmitResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.gradeAndEvaluate.Exam;
import com.example.demo.entity.gradeAndEvaluate.ExamQuestion;
import com.example.demo.entity.gradeAndEvaluate.ExamResult;
import com.example.demo.entity.gradeAndEvaluate.Grade;
import com.example.demo.entity.people.Student;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.ExamQuestionRepository;
import com.example.demo.repository.ExamResultRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.service.k1.AuditLogService;
import com.example.demo.service.k1.ExamService;
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
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    CourseClassRepository courseClassRepository;
    @Autowired
    ExamRepository examRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    ExamQuestionRepository examQuestionRepository;
    @Autowired
    ExamResultRepository examResultRepository;
    @Autowired
    AuditLogService auditLogService;
    @Autowired
    GradeRepository gradeRepository;
    @Autowired
    StudentRepository studentRepository;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ExamResponse createExam(ExamRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if (request == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        if (request.getCourseClassId() == null || request.getSubjectId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        CourseClass courseClass = courseClassRepository.findById(request.getCourseClassId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));
        var subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        Exam exam = new Exam();
        exam.setCourseClass(courseClass);
        exam.setSubject(subject);
        exam.setTypeGrade(request.getTypeGrade());
        exam.setExamDate(request.getExamDate());
        if (request.getDuration() != null) {
            exam.setDuration(request.getDuration());
        }

        Exam saved = examRepository.save(exam);
        String subjectName = saved.getSubject() != null ? saved.getSubject().getName() : null;
        auditLogService.logCreate(null, subjectName, username);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public String editExamInCourseClass(Long classId, Long examId, ExamRequest request) {
        var exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        if (exam.getCourseClass() == null
                || exam.getCourseClass().getId() == null
                || !exam.getCourseClass().getId().equals(classId)) {
            throw new AppException(ErrorCode.COURSECLASS_NOT_FOUND);
        }
        applyRequest(request, exam);
        examRepository.save(exam);
        return "Edit exam successful!";
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteExamInCourseClass(Long classId, Long examId) {
        var exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        if (exam.getCourseClass() != null
                && exam.getCourseClass().getId() != null
                && exam.getCourseClass().getId().equals(classId)) {
            exam.setCourseClass(null);
            examRepository.save(exam);
        }
        return "Delete exam successful!";
    }

    @Override
    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(exam);
    }

    @Override
    public PageResponse<ExamResponse> getListExam(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Exam> pageResult = examRepository.findAll(pageable);
        List<ExamResponse> data = pageResult.getContent()
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
    public List<ExamResponse> getExamByCourseClassName(String name) {
        List<Exam> exams = examRepository.findByCourseClass_NameContainingIgnoreCase(name);
        if (exams.isEmpty()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        return exams.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('STUDENT')")
    public ExamSubmitResponse submitExam(Long examId, ExamSubmitRequest request) {
        if (request == null || request.getAnswers() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        Long studentId = resolveStudentId();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (exam.getTypeGrade() != TypeGrade.QUIZ
                && examResultRepository.existsByExam_IdAndStudent_UserId(examId, studentId)) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }

        List<ExamQuestion> questions = examQuestionRepository.findByExam_Id(examId);
        if (questions.isEmpty()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }

        Map<Long, ExamQuestion> questionMap = new java.util.HashMap<>();
        for (ExamQuestion question : questions) {
            questionMap.put(question.getId(), question);
        }

        Map<Long, String> answers = new java.util.HashMap<>();
        for (var item : request.getAnswers()) {
            if (item.getQuestionId() == null) {
                continue;
            }
            String answer = item.getAnswer() != null ? item.getAnswer().trim().toUpperCase() : "";
            answers.put(item.getQuestionId(), answer);
        }

        for (Long questionId : answers.keySet()) {
            if (!questionMap.containsKey(questionId)) {
                throw new AppException(ErrorCode.IMFORMATION_NULL);
            }
        }

        int correct = 0;
        for (ExamQuestion question : questions) {
            String answer = answers.getOrDefault(question.getId(), "");
            if (answer.equalsIgnoreCase(question.getCorrectOption())) {
                correct++;
            }
        }
        int total = questions.size();
        double score = total == 0 ? 0 : (correct * 10.0) / total;

        Grade grade = null;
        if (exam.getTypeGrade() != TypeGrade.QUIZ) {
            grade = gradeRepository.findByStudent_UserIdAndTypeGrade(studentId, exam.getTypeGrade())
                    .orElseGet(Grade::new);
            grade.setStudent(student);
            grade.setTypeGrade(exam.getTypeGrade());
            grade.setScore(score);
            grade = gradeRepository.save(grade);
        }

        ExamResult examResult = new ExamResult();
        examResult.setExam(exam);
        examResult.setStudent(student);
        examResult.setGrade(grade);
        examResult.setScore(score);
        examResultRepository.save(examResult);

        return ExamSubmitResponse.builder()
                .examId(examId)
                .studentId(studentId)
                .typeGrade(exam.getTypeGrade())
                .totalQuestions(total)
                .correctAnswers(correct)
                .score(score)
                .build();
    }

    private ExamResponse toResponse(Exam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .courseClassId(exam.getCourseClass() != null ? exam.getCourseClass().getId() : null)
                .subjectId(exam.getSubject() != null ? exam.getSubject().getId() : null)
                .typeGrade(exam.getTypeGrade())
                .examDate(exam.getExamDate())
                .duration(exam.getDuration())
                .build();
    }

    private void applyRequest(ExamRequest request, Exam exam) {
        if (request == null) {
            return;
        }
        if (request.getSubjectId() != null) {
            var subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
            exam.setSubject(subject);
        }
        if (request.getTypeGrade() != null) {
            exam.setTypeGrade(request.getTypeGrade());
        }
        if (request.getExamDate() != null) {
            exam.setExamDate(request.getExamDate());
        }
        if (request.getDuration() != null) {
            exam.setDuration(request.getDuration());
        }
    }

    private Long resolveStudentId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return jwt.getClaim("userId");
    }
}
