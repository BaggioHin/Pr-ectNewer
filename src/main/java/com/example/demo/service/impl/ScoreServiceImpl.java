package com.example.demo.service.impl;

import com.example.demo.constant.TypeGrade;
import com.example.demo.dto.response.CourseFinalScoreResponse;
import com.example.demo.dto.response.SubjectProcessScoreResponse;
import com.example.demo.entity.courseAndAcademic.Course;
import com.example.demo.entity.courseAndAcademic.Subject;
import com.example.demo.entity.gradeAndEvaluate.Exam;
import com.example.demo.entity.gradeAndEvaluate.ExamResult;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.ExamResultRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.k1.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl implements ScoreService {

    private static final double MIDTERM_WEIGHT = 0.3;
    private static final double FINAL_WEIGHT = 0.7;
    private static final double QUIZ_BONUS = 1.0;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamResultRepository examResultRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('STUDENT')")
    public List<SubjectProcessScoreResponse> getSubjectProcessScores(Long courseId) {
        if (courseId == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        Long studentId = resolveUserId();
        if (!studentRepository.existsById(studentId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        List<Long> classIds = enrollmentRepository.findCourseClassIdsByStudentUserIdAndCourseId(studentId, courseId);
        if (classIds.isEmpty()) {
            return List.of();
        }

        List<Subject> subjects = subjectRepository.findByCourse_Id(courseId);
        if (subjects.isEmpty()) {
            return List.of();
        }
        Map<Long, Subject> subjectMap = subjects.stream()
                .collect(Collectors.toMap(Subject::getId, Function.identity()));

        List<Exam> exams = examRepository.findByCourseClass_IdIn(classIds);
        if (exams.isEmpty()) {
            return subjects.stream()
                    .map(s -> SubjectProcessScoreResponse.builder()
                            .subjectId(s.getId())
                            .subjectName(s.getName())
                            .midtermScore(null)
                            .finalScore(null)
                            .quizCompleted(false)
                            .bonus(0.0)
                            .totalScore(null)
                            .build())
                    .toList();
        }

        List<Long> examIds = exams.stream().map(Exam::getId).toList();
        Map<Long, ExamResult> resultByExamId = examResultRepository
                .findByStudent_UserIdAndExam_IdIn(studentId, examIds)
                .stream()
                .collect(Collectors.toMap(er -> er.getExam().getId(), Function.identity(), (a, b) -> a));

        Map<Long, SubjectAggregate> aggregates = new HashMap<>();
        for (Exam exam : exams) {
            if (exam.getSubject() == null || exam.getSubject().getId() == null) {
                continue;
            }
            Long subjectId = exam.getSubject().getId();
            if (!subjectMap.containsKey(subjectId)) {
                continue;
            }
            SubjectAggregate agg = aggregates.computeIfAbsent(subjectId, k -> new SubjectAggregate());

            if (exam.getTypeGrade() == TypeGrade.QUIZ) {
                agg.totalQuiz++;
                if (resultByExamId.containsKey(exam.getId())) {
                    agg.doneQuiz++;
                }
                continue;
            }

            ExamResult result = resultByExamId.get(exam.getId());
            if (result == null) {
                continue;
            }
            LocalDate examDate = exam.getExamDate();
            if (exam.getTypeGrade() == TypeGrade.MIDTERM) {
                if (agg.midtermScore == null || isAfter(examDate, agg.midtermExamDate)) {
                    agg.midtermScore = result.getScore();
                    agg.midtermExamDate = examDate;
                }
            } else if (exam.getTypeGrade() == TypeGrade.FINAL) {
                if (agg.finalScore == null || isAfter(examDate, agg.finalExamDate)) {
                    agg.finalScore = result.getScore();
                    agg.finalExamDate = examDate;
                }
            }
        }

        return subjects.stream()
                .map(subject -> buildSubjectResponse(subject, aggregates.get(subject.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('STUDENT')")
    public CourseFinalScoreResponse getCourseFinalScore(Long courseId) {
        if (courseId == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        List<SubjectProcessScoreResponse> subjects = getSubjectProcessScores(courseId);
        double sum = 0.0;
        int computedCount = 0;
        for (SubjectProcessScoreResponse item : subjects) {
            if (item.getTotalScore() != null) {
                sum += item.getTotalScore();
                computedCount++;
            }
        }
        Double average = computedCount == 0 ? null : sum / computedCount;

        return CourseFinalScoreResponse.builder()
                .courseId(course.getId())
                .courseName(course.getName())
                .averageScore(average)
                .subjectCount(subjects.size())
                .computedCount(computedCount)
                .build();
    }

    private SubjectProcessScoreResponse buildSubjectResponse(Subject subject, SubjectAggregate agg) {
        double bonus = (agg != null && agg.totalQuiz > 0 && agg.doneQuiz == agg.totalQuiz) ? QUIZ_BONUS : 0.0;
        Double midterm = agg != null ? agg.midtermScore : null;
        Double fin = agg != null ? agg.finalScore : null;
        Double total = null;
        if (midterm != null && fin != null) {
            total = (midterm * MIDTERM_WEIGHT) + (fin * FINAL_WEIGHT) + bonus;
        }
        boolean quizCompleted = agg != null && agg.totalQuiz > 0 && agg.doneQuiz == agg.totalQuiz;
        return SubjectProcessScoreResponse.builder()
                .subjectId(subject.getId())
                .subjectName(subject.getName())
                .midtermScore(midterm)
                .finalScore(fin)
                .quizCompleted(quizCompleted)
                .bonus(bonus)
                .totalScore(total)
                .build();
    }

    private boolean isAfter(LocalDate candidate, LocalDate current) {
        if (candidate == null) {
            return false;
        }
        if (current == null) {
            return true;
        }
        return candidate.isAfter(current);
    }

    private Long resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return jwt.getClaim("userId");
    }

    private static class SubjectAggregate {
        Double midtermScore;
        LocalDate midtermExamDate;
        Double finalScore;
        LocalDate finalExamDate;
        int totalQuiz;
        int doneQuiz;
    }
}
