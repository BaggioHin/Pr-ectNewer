package com.example.demo.service.impl;

import com.example.demo.constant.TeachingAssignmentStatus;
import com.example.demo.dto.request.TeachingAssignmentRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.TeachingAssignmentResponse;
import com.example.demo.dto.response.TeachingAssignmentSummary;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.classAndLearn.TeachingAssignment;
import com.example.demo.entity.people.Teacher;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.TeachingAssignmentRepository;
import com.example.demo.service.k1.TeachingAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeachingAssignmentImpl implements TeachingAssignmentService {

    @Autowired
    private TeachingAssignmentRepository teachingAssignmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseClassRepository courseClassRepository;

    @Override
    public TeachingAssignmentResponse getTeachingAssignmentById(Long id) {
        TeachingAssignment assignment = teachingAssignmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(assignment);
    }

    @Override
    public PageResponse<TeachingAssignmentResponse> getListTeachingAssignment(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<TeachingAssignment> pageResult = teachingAssignmentRepository.findAll(pageable);

        List<TeachingAssignmentResponse> data = pageResult.getContent()
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
    @PreAuthorize("hasRole('ADMIN')")
    public TeachingAssignmentResponse addTeachingAssignment(TeachingAssignmentRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        CourseClass courseClass = courseClassRepository.findById(request.getCourseClassId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));

        if (teachingAssignmentRepository.existsByTeacher_UserIdAndCourseClass_Id(
                teacher.getUserId(),
                courseClass.getId()
        )) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }

        TeachingAssignment assignment = new TeachingAssignment();
        assignment.setTeacher(teacher);
        assignment.setCourseClass(courseClass);
        assignment.setStatus(TeachingAssignmentStatus.TEACHING);
        assignment.setEnrolledAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());

        TeachingAssignment saved = teachingAssignmentRepository.save(assignment);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TeachingAssignmentResponse changeStatusTeachingAssignment(Long id, String status) {
        TeachingAssignment assignment = teachingAssignmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        TeachingAssignmentStatus nextStatus = parseStatus(status);
        assignment.setStatus(nextStatus);
        assignment.setUpdatedAt(LocalDateTime.now());
        if (nextStatus != TeachingAssignmentStatus.TEACHING && assignment.getEndAt() == null) {
            assignment.setEndAt(LocalDateTime.now());
        }
        TeachingAssignment saved = teachingAssignmentRepository.save(assignment);
        return toResponse(saved);
    }

    private TeachingAssignmentResponse toResponse(TeachingAssignment assignment) {
        CourseClass courseClass = assignment.getCourseClass();
//        Teacher teacher = assignment.getTeacher();

        TeachingAssignmentSummary summary = TeachingAssignmentSummary.builder()
                .id(courseClass != null ? courseClass.getId() : null)
                .name(courseClass != null ? courseClass.getName() : null)
                .startDate(courseClass != null ? courseClass.getStartDay() : null)
                .endDate(courseClass != null ? courseClass.getEndDay() : null)
                .build();

        return TeachingAssignmentResponse.builder()
                .id(assignment.getId())
                .status(assignment.getStatus())
                .enrolledAt(assignment.getEnrolledAt())
                .endAt(assignment.getEndAt())
                .teachingAssignment(summary)
                .build();
    }

    private TeachingAssignmentStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }
        try {
            return TeachingAssignmentStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }
    }
}
