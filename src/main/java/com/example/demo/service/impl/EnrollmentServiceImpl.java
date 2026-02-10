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
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.CourseClassMapper;
import com.example.demo.mapper.EnrollmentMapper;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.StudentProgressRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.k1.EnrollmentService;
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

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourseClass(courseClass);
        enrollment.setStatus(EnrollmentStatus.STUDYING);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setUpdatedAt(LocalDate.now());
        enrollment.setEndAt(courseClass.getEndDay());
        enrollment.setCreateById(userCreate.getSale().getCode());
        Enrollment saved = enrollmentRepository.save(enrollment);
        createStudentProgressIfMissing(student, courseClass);
        return toResponse(saved);
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
}
