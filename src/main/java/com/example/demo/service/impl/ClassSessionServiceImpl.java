package com.example.demo.service.impl;

import com.example.demo.dto.request.ClassSessionRequest;
import com.example.demo.dto.response.ClassSessionResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.classAndLearn.ClassSchedule;
import com.example.demo.entity.classAndLearn.ClassSession;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.ClassScheduleRepository;
import com.example.demo.repository.ClassSessionRepository;
import com.example.demo.service.k1.ClassSessionService;
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

        ClassSession saved = classSessionRepository.save(session);
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
        ClassSession saved = classSessionRepository.save(session);
        return toResponse(saved);
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
                .build();
    }
}
