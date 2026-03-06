package com.example.demo.service.impl;

import com.example.demo.constant.StatusAttendance;
import com.example.demo.constant.StatusClassSession;
import com.example.demo.dto.response.ClassProgressResponse;
import com.example.demo.dto.response.StudentClassProgressResponse;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ClassSessionRepository;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.k1.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgressServiceImpl implements ProgressService {

    @Autowired
    private CourseClassRepository courseClassRepository;

    @Autowired
    private ClassSessionRepository classSessionRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','SALER','STUDENT')")
    public ClassProgressResponse getClassProgress(Long courseClassId) {
        if (courseClassId == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        CourseClass courseClass = courseClassRepository.findById(courseClassId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));

        long completed = classSessionRepository.countByClassSchedule_CourseClass_IdAndStatusClassSession(
                courseClass.getId(),
                StatusClassSession.DONE
        );
        long total = classSessionRepository.countByCourseClassIdAndStatusNot(
                courseClass.getId(),
                StatusClassSession.CACELED
        );

        double percent = total == 0 ? 0.0 : (completed * 100.0) / total;
        return ClassProgressResponse.builder()
                .courseClassId(courseClass.getId())
                .completedSessions((int) completed)
                .totalSessions((int) total)
                .progressPercent(percent)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('STUDENT')")
    public StudentClassProgressResponse getMyClassProgress(Long courseClassId) {
        if (courseClassId == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        CourseClass courseClass = courseClassRepository.findById(courseClassId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));

        Long studentId = resolveUserId();
        if (!studentRepository.existsById(studentId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        long completed = classSessionRepository.countByClassSchedule_CourseClass_IdAndStatusClassSession(
                courseClass.getId(),
                StatusClassSession.DONE
        );
        long attended = attendanceRepository.countAttendedSessions(
                studentId,
                courseClass.getId(),
                StatusClassSession.DONE,
                List.of(StatusAttendance.PRESENT, StatusAttendance.LATE)
        );
        double percent = completed == 0 ? 0.0 : (attended * 100.0) / completed;

        return StudentClassProgressResponse.builder()
                .courseClassId(courseClass.getId())
                .studentId(studentId)
                .attendedSessions((int) attended)
                .completedSessions((int) completed)
                .progressPercent(percent)
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
