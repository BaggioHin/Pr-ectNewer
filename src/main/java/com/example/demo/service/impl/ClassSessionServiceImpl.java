package com.example.demo.service.impl;

import com.example.demo.dto.request.ClassSessionRequest;
import com.example.demo.dto.response.ClassSessionResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.constant.StatusClassSession;
import com.example.demo.constant.NotificationRefType;
import com.example.demo.constant.NotificationType;
import com.example.demo.constant.EnrollmentStatus;
import com.example.demo.entity.classAndLearn.ClassSchedule;
import com.example.demo.entity.classAndLearn.ClassSession;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.ClassScheduleRepository;
import com.example.demo.repository.ClassSessionRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.service.k1.ClassSessionService;
import com.example.demo.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassSessionServiceImpl implements ClassSessionService {
    @Autowired
    ClassSessionRepository classSessionRepository;
    @Autowired
    ClassScheduleRepository classScheduleRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    NotificationService notificationService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public ClassSessionResponse getClassSessionById(Long id) {
        ClassSession session = classSessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(session);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public PageResponse<ClassSessionResponse> getListClassSession(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ClassSession> pageResult = classSessionRepository.findAll(pageable);
        List<ClassSessionResponse> data = pageResult.getContent()
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
    public ClassSessionResponse addClassSession(ClassSessionRequest request) {
        if (request == null || request.getClassScheduleId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        ClassSchedule schedule = classScheduleRepository.findById(request.getClassScheduleId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        ClassSession session = new ClassSession();
        session.setClassSchedule(schedule);
        applyRequest(session, request);
        if (request.getMakeup() == null && request.getMakeupForSessionId() == null) {
            session.setMakeup(false);
        }
        normalizeAndValidateMakeup(session);

        ClassSession saved = classSessionRepository.save(session);
        notifyStudentsNewSession(saved);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ClassSessionResponse editClassSession(Long id, ClassSessionRequest request) {
        ClassSession session = classSessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        if (request != null && request.getClassScheduleId() != null) {
            ClassSchedule schedule = classScheduleRepository.findById(request.getClassScheduleId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
            session.setClassSchedule(schedule);
        }
        applyRequest(session, request);
        normalizeAndValidateMakeup(session);
        ClassSession saved = classSessionRepository.save(session);
        notifyStudentsNewSession(saved);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ClassSessionResponse addMakeupSession(ClassSessionRequest request) {
        if (request == null || request.getClassScheduleId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        request.setMakeup(true);
        ClassSchedule schedule = classScheduleRepository.findById(request.getClassScheduleId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        ClassSession session = new ClassSession();
        session.setClassSchedule(schedule);
        applyRequest(session, request);
        session.setMakeup(true);
        normalizeAndValidateMakeup(session);

        ClassSession saved = classSessionRepository.save(session);
        notifyStudentsNewSession(saved);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ClassSessionResponse editMakeupSession(Long id, ClassSessionRequest request) {
        ClassSession session = classSessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        if (!session.isMakeup()) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }
        if (request != null && request.getClassScheduleId() != null) {
            ClassSchedule schedule = classScheduleRepository.findById(request.getClassScheduleId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
            session.setClassSchedule(schedule);
        }
        if (request == null) {
            request = new ClassSessionRequest();
        }
        request.setMakeup(true);
        applyRequest(session, request);
        session.setMakeup(true);
        normalizeAndValidateMakeup(session);
        ClassSession saved = classSessionRepository.save(session);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public ClassSessionResponse getMakeupSessionById(Long id) {
        ClassSession session = classSessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        if (!session.isMakeup()) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }
        return toResponse(session);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public List<ClassSessionResponse> getMakeupSessionsByCourseClass(Long courseClassId) {
        if (courseClassId == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        return classSessionRepository.findMakeupSessionsByCourseClassId(courseClassId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteClassSession(Long id) {
        ClassSession session = classSessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        classSessionRepository.delete(session);
        return "Delete successful!";
    }

    private void applyRequest(ClassSession session, ClassSessionRequest request) {
        if (request == null) {
            return;
        }
        if (request.getDate() != null) {
            session.setDate(request.getDate());
        }
        if (request.getStartTime() != null) {
            session.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            session.setEndTime(request.getEndTime());
        }
        if (request.getTopic() != null) {
            session.setTopic(request.getTopic());
        }
        if (request.getStatusClassSession() != null) {
            session.setStatusClassSession(request.getStatusClassSession());
        }
        if (request.getMakeup() != null) {
            session.setMakeup(request.getMakeup());
        }
        if (request.getMakeupForSessionId() != null) {
            ClassSession original = classSessionRepository.findById(request.getMakeupForSessionId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
            session.setMakeupForSession(original);
            session.setMakeup(true);
        }
    }

    private void normalizeAndValidateMakeup(ClassSession session) {
        if (session.getMakeupForSession() != null) {
            session.setMakeup(true);
        }
        if (!session.isMakeup()) {
            return;
        }
        if (session.getClassSchedule() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        ClassSession original = session.getMakeupForSession();
        if (original == null) {
            return;
        }
        if (session.getId() != null && session.getId().equals(original.getId())) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        if (original.getClassSchedule() == null
                || original.getClassSchedule().getCourseClass() == null
                || session.getClassSchedule().getCourseClass() == null
                || !session.getClassSchedule().getCourseClass().getId()
                .equals(original.getClassSchedule().getCourseClass().getId())) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        if (original.getStatusClassSession() != StatusClassSession.CACELED) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }
    }

    private ClassSessionResponse toResponse(ClassSession session) {
        return ClassSessionResponse.builder()
                .id(session.getId())
                .classScheduleId(session.getClassSchedule() != null ? session.getClassSchedule().getId() : null)
                .date(session.getDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .topic(session.getTopic())
                .statusClassSession(session.getStatusClassSession())
                .makeup(session.isMakeup())
                .makeupForSessionId(session.getMakeupForSession() != null ? session.getMakeupForSession().getId() : null)
                .build();
    }

    private void notifyStudentsNewSession(ClassSession session) {
        if (session == null || session.getClassSchedule() == null || session.getClassSchedule().getCourseClass() == null) {
            return;
        }
        Long classId = session.getClassSchedule().getCourseClass().getId();
        if (classId == null) {
            return;
        }
        List<Long> userIds = enrollmentRepository.findStudentUserIdsByCourseClassIdAndStatuses(
                classId,
                List.of(EnrollmentStatus.STUDYING)
        );
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        String className = session.getClassSchedule().getCourseClass().getName();
        String title = session.isMakeup() ? "Lich hoc bu moi" : "Lich hoc moi";
        String content;
        if (className == null || className.isBlank()) {
            content = session.isMakeup()
                    ? "Lop cua ban co lich hoc bu moi."
                    : "Lop cua ban co lich hoc moi.";
        } else {
            content = session.isMakeup()
                    ? "Lop " + className + " co lich hoc bu moi."
                    : "Lop " + className + " co lich hoc moi.";
        }
        notificationService.notifyUsers(
                title,
                content,
                NotificationType.CLASS_SESSION_CREATED,
                NotificationRefType.CLASS_SESSION,
                session.getId(),
                userIds
        );
    }
}
