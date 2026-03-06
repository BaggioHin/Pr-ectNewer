package com.example.demo.service.impl;

import com.example.demo.constant.TypeGrade;
import com.example.demo.constant.NotificationRefType;
import com.example.demo.constant.NotificationType;
import com.example.demo.constant.EnrollmentStatus;
import com.example.demo.dto.request.ExamRequest;
import com.example.demo.dto.request.ExamSubmitRequest;
import com.example.demo.dto.response.ExamResponse;
import com.example.demo.dto.response.ExamSubmitResponse;
import com.example.demo.dto.response.ExamStudentResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.gradeAndEvaluate.Exam;
import com.example.demo.entity.gradeAndEvaluate.ExamQuestion;
import com.example.demo.entity.gradeAndEvaluate.ExamResult;
import com.example.demo.entity.gradeAndEvaluate.Grade;
import com.example.demo.entity.people.Student;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.ExamQuestionRepository;
import com.example.demo.repository.ExamResultRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.k1.AuditLogService;
import com.example.demo.service.k1.ExamService;
import com.example.demo.service.notification.NotificationService;
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
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    NotificationService notificationService;

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
        Long creatorId = resolveUserId();
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        exam.setCreatedBy(creator);

        Exam saved = examRepository.save(exam);
        String subjectName = saved.getSubject() != null ? saved.getSubject().getName() : null;
        auditLogService.logCreate(null, subjectName, username);
        notifyStudentsNewExam(saved);
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
    @PreAuthorize("hasRole('STUDENT')")
    public PageResponse<ExamStudentResponse> getMyExams(String status, int page, int size) {
        String normalized = status == null ? "all" : status.trim().toLowerCase();
        if (!normalized.equals("all") && !normalized.equals("done") && !normalized.equals("not_done")) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }

        Long studentId = resolveStudentId();
        if (!studentRepository.existsById(studentId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        List<Long> classIds = enrollmentRepository.findCourseClassIdsByStudentUserId(studentId);
        if (classIds.isEmpty()) {
            return new PageResponse<>(
                    List.of(),
                    page,
                    size,
                    0,
                    0
            );
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("examDate").descending().and(Sort.by("id").descending()));
        Page<Exam> pageResult;
        if (normalized.equals("done")) {
            pageResult = examRepository.findDoneByStudent(classIds, studentId, pageable);
        } else if (normalized.equals("not_done")) {
            pageResult = examRepository.findNotDoneByStudent(classIds, studentId, pageable);
        } else {
            pageResult = examRepository.findByCourseClass_IdIn(classIds, pageable);
        }

        List<Long> examIds = pageResult.getContent()
                .stream()
                .map(Exam::getId)
                .toList();
        Map<Long, ExamResult> resultMap = examIds.isEmpty()
                ? java.util.Collections.emptyMap()
                : examResultRepository.findByStudent_UserIdAndExam_IdIn(studentId, examIds)
                .stream()
                .collect(Collectors.toMap(er -> er.getExam().getId(), Function.identity(), (a, b) -> a));

        List<ExamStudentResponse> data = pageResult.getContent()
                .stream()
                .map(exam -> toStudentResponse(exam, resultMap.get(exam.getId())))
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

        ExamResult existingResult = examResultRepository
                .findByExam_IdAndStudent_UserId(examId, studentId)
                .orElse(null);
        if (exam.getTypeGrade() != TypeGrade.QUIZ && existingResult != null) {
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

        ExamResult examResult;
        if (exam.getTypeGrade() == TypeGrade.QUIZ && existingResult != null) {
            examResult = existingResult;
        } else {
            examResult = new ExamResult();
            examResult.setExam(exam);
            examResult.setStudent(student);
        }
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

    private ExamStudentResponse toStudentResponse(Exam exam, ExamResult result) {
        return ExamStudentResponse.builder()
                .id(exam.getId())
                .courseClassId(exam.getCourseClass() != null ? exam.getCourseClass().getId() : null)
                .courseClassName(exam.getCourseClass() != null ? exam.getCourseClass().getName() : null)
                .courseId(exam.getCourseClass() != null && exam.getCourseClass().getCourse() != null
                        ? exam.getCourseClass().getCourse().getId()
                        : null)
                .courseName(exam.getCourseClass() != null && exam.getCourseClass().getCourse() != null
                        ? exam.getCourseClass().getCourse().getName()
                        : null)
                .subjectId(exam.getSubject() != null ? exam.getSubject().getId() : null)
                .subjectName(exam.getSubject() != null ? exam.getSubject().getName() : null)
                .typeGrade(exam.getTypeGrade())
                .examDate(exam.getExamDate())
                .duration(exam.getDuration())
                .done(result != null)
                .score(result != null ? result.getScore() : null)
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
        return resolveUserId();
    }

    private Long resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return jwt.getClaim("userId");
    }

    private void notifyStudentsNewExam(Exam exam) {
        if (exam == null || exam.getCourseClass() == null || exam.getCourseClass().getId() == null) {
            return;
        }
        Long classId = exam.getCourseClass().getId();
        List<Long> userIds = enrollmentRepository.findStudentUserIdsByCourseClassIdAndStatuses(
                classId,
                List.of(EnrollmentStatus.STUDYING)
        );
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        String className = exam.getCourseClass().getName();
        String title = "Bai kiem tra moi";
        String content = className == null || className.isBlank()
                ? "Lop cua ban co bai kiem tra moi."
                : "Lop " + className + " co bai kiem tra moi.";
        notificationService.notifyUsers(
                title,
                content,
                NotificationType.EXAM_PUBLISHED,
                NotificationRefType.EXAM,
                exam.getId(),
                userIds
        );
    }
}
