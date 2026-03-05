package com.example.demo.service.impl;

import com.example.demo.dto.request.AttendanceRequest;
import com.example.demo.dto.response.AttendanceResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.classAndLearn.Attendance;
import com.example.demo.entity.classAndLearn.AttendanceId;
import com.example.demo.entity.classAndLearn.ClassSession;
import com.example.demo.entity.people.Student;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ClassSessionRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.k1.AttendanceService;
import com.example.demo.service.k1.AdminStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    AttendanceRepository attendanceRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    ClassSessionRepository classSessionRepository;
    @Autowired
    AdminStatsService adminStatsService;

    @Override
    public AttendanceResponse getAttendanceById(Long classSessionId, Long studentId) {
        Attendance attendance = attendanceRepository.findById(new AttendanceId(classSessionId, studentId))
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(attendance);
    }

    @Override
    public PageResponse<AttendanceResponse> getListAttendance(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("classSession.id").descending());
        Page<Attendance> pageResult = attendanceRepository.findAll(pageable);
        List<AttendanceResponse> data = pageResult.getContent()
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
    @PreAuthorize("hasAnyRole('ADMIN','SALER')")
    public AttendanceResponse addAttendance(AttendanceRequest request) {
        if (request == null || request.getClassSessionId() == null || request.getStudentId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        AttendanceId id = new AttendanceId(request.getClassSessionId(), request.getStudentId());
        if (attendanceRepository.existsById(id)) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        ClassSession classSession = classSessionRepository.findById(request.getClassSessionId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        Attendance attendance = new Attendance();
        attendance.setId(id);
        attendance.setStudent(student);
        attendance.setClassSession(classSession);
        attendance.setStatusAttendance(request.getStatusAttendance());

        Attendance saved = attendanceRepository.save(attendance);
        adminStatsService.refreshMonthlyStats(java.time.LocalDateTime.now());
        return toResponse(saved);
    }

    @Override
    public AttendanceResponse editAttendance(Long classSessionId, Long studentId, AttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(new AttendanceId(classSessionId, studentId))
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        if (request != null && request.getStatusAttendance() != null) {
            attendance.setStatusAttendance(request.getStatusAttendance());
        }
        Attendance saved = attendanceRepository.save(attendance);
        return toResponse(saved);
    }

    @Override
    public String deleteAttendance(Long classSessionId, Long studentId) {
        Attendance attendance = attendanceRepository.findById(new AttendanceId(classSessionId, studentId))
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        attendanceRepository.delete(attendance);
        adminStatsService.refreshMonthlyStats(java.time.LocalDateTime.now());
        return "Delete successful!";
    }

    private AttendanceResponse toResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .classSessionId(attendance.getId() != null ? attendance.getId().getClassSessionId() : null)
                .studentId(attendance.getId() != null ? attendance.getId().getStudentId() : null)
                .statusAttendance(attendance.getStatusAttendance())
                .build();
    }
}
