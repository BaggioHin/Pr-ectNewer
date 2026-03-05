package com.example.demo.service.impl;

import com.example.demo.dto.request.CourseClassRequest;
import com.example.demo.dto.response.CourseClassResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.classAndLearn.ClassSchedule;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.CourseClassMapper;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.ClassScheduleRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.k1.AuditLogService;
import com.example.demo.service.k1.CourseClassService;
import jakarta.transaction.Transactional;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CourseClassServiceImpl implements CourseClassService {

    @Autowired
    CourseClassRepository courseClassRepository;
    @Autowired
    CourseClassMapper courseClassMapper;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    AuditLogService auditLogService;


    @PreAuthorize("isAuthenticated()")
    @Override
    public CourseClassResponse getCourseClassById() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long userId = jwt.getClaim("userId");
        CourseClass courseClass = courseClassRepository.findById(userId).get();
        return courseClassMapper.entityToResponse(courseClass);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public List<CourseClassResponse> getCourseClassByName(String name) {
        List<CourseClass> courseClasses = courseClassRepository.findByName(name);
        return courseClassMapper.entityToListResponse(courseClasses);
    }


    @PreAuthorize("isAuthenticated()")
    @Override
    @Transactional
    public PageResponse<CourseClassResponse> getListCourseClass(int page,int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CourseClass> pageResult = courseClassRepository.findAll(pageable);
        List<CourseClassResponse> data = courseClassMapper.entityToListResponse(pageResult.getContent());

        return new PageResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public PageResponse<CourseClassResponse> getCourseClassesByCourseId(Long courseId, int page, int size) {
        if (courseId == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CourseClass> pageResult = courseClassRepository.findByCourse_Id(courseId, pageable);
        List<CourseClassResponse> data = courseClassMapper.entityToListResponse(pageResult.getContent());

        return new PageResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @PreAuthorize("hasAnyRole('SALER','ADMIN')")
    @Override
    public CourseClassResponse addCourseClass(CourseClassRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if (request == null || request.getCourseId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        CourseClass courseClass = new CourseClass();
        courseClassMapper.requestToEntity(request, courseClass);
        var course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        courseClass.setCourse(course);
        courseClass.setStatusCourse(request.getStatusCourse());
        courseClass.setDescription(request.getDescription());
        if (request.getStartDate() != null) {
            courseClass.setStartDay(LocalDateTime.of(request.getStartDate(), LocalTime.MIDNIGHT));
        }
        if (courseClass.getClassCode() == null || courseClass.getClassCode().isBlank()) {
            courseClass.setClassCode(generateClassCode());
        }


        CourseClass saved = courseClassRepository.save(courseClass);
        auditLogService.logCreate(null, saved.getName(), username);
        return courseClassMapper.entityToResponse(saved);
    }

    @PreAuthorize("hasAnyRole('SALER','ADMIN')")
    @Override
    public CourseClassResponse editCourseClass(Long id, CourseClassRequest request) {

        CourseClass courseClass = courseClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));

        courseClassMapper.requestToEntity(request, courseClass);
        if (request != null && request.getCourseId() != null) {
            var course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
            courseClass.setCourse(course);
        }
        if (request != null && request.getStartDate() != null) {
            courseClass.setStartDay(LocalDateTime.of(request.getStartDate(), LocalTime.MIDNIGHT));
        }

        CourseClass saved = courseClassRepository.save(courseClass);
        return courseClassMapper.entityToResponse(saved);
    }


    @PreAuthorize("hasAnyRole('SALER','ADMIN')")
    @Override
    public String deleteCourseClass(Long id) {
        courseClassRepository.deleteById(id);
        return "Delete successful!";
    }

    private String generateClassCode() {
        String prefix = "CLS";
        String maxCode = courseClassRepository.findMaxClassCodeByPrefix(prefix);
        return nextSequentialCode(prefix, maxCode, 4);
    }

    private String nextSequentialCode(String prefix, String maxCode, int width) {
        int next = 1;
        if (maxCode != null && maxCode.startsWith(prefix)) {
            String numericPart = maxCode.substring(prefix.length());
            try {
                next = Integer.parseInt(numericPart) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }
        return prefix + String.format("%0" + width + "d", next);
    }
}
