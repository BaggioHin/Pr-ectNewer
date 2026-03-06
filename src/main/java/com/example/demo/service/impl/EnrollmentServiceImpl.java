package com.example.demo.service.impl;

import com.example.demo.constant.CreateType;
import com.example.demo.constant.EnrollmentStatus;
import com.example.demo.constant.StatusCourse;
import com.example.demo.dto.response.EnrollmentReponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.loginAndProcess.Enrollment;
import com.example.demo.entity.loginAndProcess.StudentProgress;
import com.example.demo.entity.loginAndProcess.StudentProgressId;
import com.example.demo.entity.people.Student;
import com.example.demo.entity.sales.Saler;
import com.example.demo.entity.statistics.CourseStudentStats;
import com.example.demo.entity.statistics.MonthlyRevenueStats;
import com.example.demo.entity.statistics.SalerRevenueStats;
import com.example.demo.entity.sales.SalerTransaction;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.CourseClassMapper;
import com.example.demo.mapper.EnrollmentMapper;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.CourseStudentStatsRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.MonthlyRevenueStatsRepository;
import com.example.demo.repository.SalerRevenueStatsRepository;
import com.example.demo.repository.SalerTransactionRepository;
import com.example.demo.repository.SalerRepository;
import com.example.demo.repository.StudentProgressRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.k1.EnrollmentService;
import com.example.demo.service.k1.AdminStatsService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final int MAX_CLASS_SIZE = 25;

    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    EnrollmentMapper enrollmentMapper;
    @Autowired
    CourseClassMapper courseClassMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CourseClassRepository courseClassRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    StudentProgressRepository studentProgressRepository;
    @Autowired
    AdminStatsService adminStatsService;
    @Autowired
    MonthlyRevenueStatsRepository monthlyRevenueStatsRepository;
    @Autowired
    CourseStudentStatsRepository courseStudentStatsRepository;
    @Autowired
    SalerRevenueStatsRepository salerRevenueStatsRepository;
    @Autowired
    SalerTransactionRepository salerTransactionRepository;
    @Autowired
    SalerRepository salerRepository;

    @Override
    @PreAuthorize("hasAuthority('ADMIN') and hasAuthority('SALER')")
    public EnrollmentReponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.IMFORMATION_NULL));
        return enrollmentMapper.entityToResponse(enrollment);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN') and hasAuthority('SALER')")
    public PageResponse<EnrollmentReponse> getListEnrollment(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Enrollment> pageResult =
                enrollmentRepository.findAll(pageable);

        List<EnrollmentReponse> data = pageResult.getContent()
                .stream()
                .map(enrollment -> EnrollmentReponse.builder()
                        .id(enrollment.getId())
                        .enrolledAt(enrollment.getEnrolledAt())
                        .endAt(enrollment.getEndAt())
                        .status(enrollment.getStatus())
                        .courseClass(
                                courseClassMapper.entityToSummary(
                                        enrollment.getCourseClass()
                                )
                        )
                        .build())
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
    @PreAuthorize("hasRole('SALER')")
    @Transactional
    public EnrollmentReponse addEnrollment(Long courseClassId,Long studentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long userId = jwt.getClaim("userId");
        var userCreate = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return createEnrollmentInternal(userCreate, courseClassId, studentId);
    }

    @Override
    @Transactional
    public EnrollmentReponse createEnrollmentForPayment(Long courseClassId, Long studentId, Long salerUserId) {
        if (salerUserId == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        var userCreate = userRepository.findById(salerUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return createEnrollmentInternal(userCreate, courseClassId, studentId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public EnrollmentReponse changeStatusEnrollment(Long id, String status) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        EnrollmentStatus nextStatus = parseStatus(status);
        enrollment.setStatus(nextStatus);
        enrollment.setUpdatedAt(LocalDate.now());
        Enrollment saved = enrollmentRepository.save(enrollment);
        adminStatsService.refreshMonthlyStats(LocalDateTime.now());
        if (saved.getEnrolledAt() != null) {
            adminStatsService.refreshMonthlyStats(saved.getEnrolledAt());
        }
        return toResponse(saved);
    }

    private EnrollmentStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }
        try {
            return EnrollmentStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }
    }

    private EnrollmentReponse toResponse(Enrollment enrollment) {
        return EnrollmentReponse.builder()
                .id(enrollment.getId())
                .enrolledAt(enrollment.getEnrolledAt())
                .endAt(enrollment.getEndAt())
                .status(enrollment.getStatus())
                .courseClass(courseClassMapper.entityToSummary(enrollment.getCourseClass()))
                .build();
    }

    private EnrollmentReponse createEnrollmentInternal(User userCreate, Long courseClassId, Long studentId) {
        if (courseClassId == null || studentId == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        CourseClass courseClass = courseClassRepository.findById(courseClassId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));
        if (courseClass.getStatusCourse() != StatusCourse.OPEN) {
            throw new AppException(ErrorCode.COURSECLASS_CLOSED);
        }
        if (enrollmentRepository.existsByStudent_UserIdAndCourseClass_Id(student.getUserId(), courseClass.getId())) {
            throw new AppException(ErrorCode.ENROLLMENT_EXISTED);
        }
        if (enrollmentRepository.countByCourseClass_Id(courseClass.getId()) >= MAX_CLASS_SIZE) {
            throw new AppException(ErrorCode.COURSECLASS_FULL);
        }

        Saler saler = resolveSaler(userCreate);
        if (saler == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourseClass(courseClass);
        enrollment.setStatus(EnrollmentStatus.STUDYING);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setUpdatedAt(LocalDate.now());
        enrollment.setEndAt(courseClass.getEndDay());
        enrollment.setCreateById(saler.getCode());
        Enrollment saved = enrollmentRepository.save(enrollment);
        createStudentProgressIfMissing(student, courseClass);
        updateMonthlyRevenueStats(saved);
        updateCourseStudentStats(saved);
        updateSalerRevenueStats(userCreate, saved);
        recordSalerTransaction(userCreate, saved);
        adminStatsService.refreshMonthlyStats(saved.getEnrolledAt());
        return toResponse(saved);
    }

    private void createStudentProgressIfMissing(Student student, CourseClass courseClass) {
        StudentProgressId id = new StudentProgressId(student.getUserId(), courseClass.getId());
        if (studentProgressRepository.existsById(id)) {
            return;
        }
        StudentProgress progress = new StudentProgress();
        progress.setId(id);
        progress.setStudent(student);
        progress.setCourseClass(courseClass);
        progress.setCompletionPercent(0.0);
        progress.setUpdatedAt(LocalDateTime.now());
        studentProgressRepository.save(progress);
    }

    private void updateMonthlyRevenueStats(Enrollment enrollment) {
        if (enrollment == null || enrollment.getEnrolledAt() == null) {
            return;
        }
        Long price = resolveCoursePrice(enrollment);
        if (price == null) {
            return;
        }
        LocalDateTime enrolledAt = enrollment.getEnrolledAt();
        int year = enrolledAt.getYear();
        int month = enrolledAt.getMonthValue();

        MonthlyRevenueStats stats = monthlyRevenueStatsRepository
                .findByStatYearAndStatMonth(year, month)
                .orElseGet(() -> MonthlyRevenueStats.builder()
                        .statYear(year)
                        .statMonth(month)
                        .totalRevenue(0L)
                        .build());

        stats.setTotalRevenue(stats.getTotalRevenue() + price);
        monthlyRevenueStatsRepository.save(stats);
    }

    private void updateCourseStudentStats(Enrollment enrollment) {
        if (enrollment == null || enrollment.getCourseClass() == null) {
            return;
        }
        var course = enrollment.getCourseClass().getCourse();
        if (course == null) {
            return;
        }
        CourseStudentStats stats = courseStudentStatsRepository
                .findByCourse(course)
                .orElseGet(() -> CourseStudentStats.builder()
                        .course(course)
                        .totalStudents(0L)
                        .build());
        stats.setTotalStudents(stats.getTotalStudents() + 1);
        courseStudentStatsRepository.save(stats);
    }

    private void updateSalerRevenueStats(User userCreate, Enrollment enrollment) {
        if (userCreate == null || enrollment == null) {
            return;
        }
        var saler = resolveSaler(userCreate);
        if (saler == null) {
            return;
        }
        Long price = resolveCoursePrice(enrollment);
        if (price == null) {
            return;
        }
        SalerRevenueStats stats = salerRevenueStatsRepository
                .findBySaler(saler)
                .orElseGet(() -> SalerRevenueStats.builder()
                        .saler(saler)
                        .totalRevenue(0L)
                        .totalDeals(0L)
                        .build());
        stats.setTotalRevenue(stats.getTotalRevenue() + price);
        salerRevenueStatsRepository.save(stats);
    }

    private void recordSalerTransaction(User userCreate, Enrollment enrollment) {
        if (userCreate == null || enrollment == null) {
            return;
        }
        var saler = resolveSaler(userCreate);
        if (saler == null) {
            return;
        }
        Long price = resolveCoursePrice(enrollment);
        if (price == null) {
            return;
        }
        var student = enrollment.getStudent();
        String studentName = null;
        if (student != null && student.getUser() != null && student.getUser().getProfile() != null) {
            studentName = student.getUser().getProfile().getName();
        }
        var courseClass = enrollment.getCourseClass();
        String courseName = null;
        if (courseClass != null && courseClass.getCourse() != null) {
            courseName = courseClass.getCourse().getName();
        }
        Long studentId = student != null ? student.getUserId() : null;
        Long courseClassId = courseClass != null ? courseClass.getId() : null;
        if (studentName == null) {
            studentName = "UNKNOWN";
        }
        if (courseName == null) {
            courseName = "UNKNOWN";
        }
        SalerTransaction transaction = SalerTransaction.builder()
                .saler(saler)
                .studentName(studentName)
                .studentId(studentId)
                .courseName(courseName)
                .courseClassId(courseClassId)
                .amount(price)
                .occurredAt(enrollment.getEnrolledAt() != null ? enrollment.getEnrolledAt() : LocalDateTime.now())
                .build();
        salerTransactionRepository.save(transaction);
    }

    private Saler resolveSaler(User userCreate) {
        if (userCreate == null) {
            return null;
        }
        var resolvedSaler = userCreate.getSale();
        if (resolvedSaler != null) {
            return resolvedSaler;
        }
        return salerRepository.findByUser_Id(userCreate.getId());
    }

    private Long resolveCoursePrice(Enrollment enrollment) {
        if (enrollment == null || enrollment.getCourseClass() == null) {
            return null;
        }
        var course = enrollment.getCourseClass().getCourse();
        if (course == null || course.getPrice() == null) {
            return null;
        }
        return course.getPrice();
    }
}
