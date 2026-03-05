package com.example.demo.service.impl;

import com.example.demo.dto.request.ScheduleRequest;
import com.example.demo.dto.request.ScheduleSlotRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ScheduleResponse;
import com.example.demo.dto.response.ScheduleSlotResponse;
import com.example.demo.entity.classAndLearn.ClassSchedule;
import com.example.demo.entity.classAndLearn.ClassSession;
import com.example.demo.entity.classAndLearn.ClassScheduleSlot;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.classAndLearn.TeachingAssignment;
import com.example.demo.entity.loginAndProcess.Enrollment;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.ClassScheduleRepository;
import com.example.demo.repository.ClassSessionRepository;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.TeachingAssignmentRepository;
import com.example.demo.service.k1.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    CourseClassRepository courseClassRepository;
    @Autowired
    ClassScheduleRepository classScheduleRepository;
    @Autowired
    ClassSessionRepository classSessionRepository;
    @Autowired
    TeachingAssignmentRepository teachingAssignmentRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Override
    public ScheduleResponse addScheduleInCourseClass(ScheduleRequest request) {
        if (request == null || request.getCourseClassId() == null
                || request.getStartDate() == null
                || request.getSlots() == null
                || request.getSlots().isEmpty()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        CourseClass courseClass = courseClassRepository.findById(request.getCourseClassId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));
        if (courseClass.getCourse() == null || courseClass.getCourse().getTotalSessions() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }

        ClassSchedule schedule = courseClass.getSchedule();
        if (schedule == null) {
            schedule = new ClassSchedule();
            schedule.setCourseClass(courseClass);
        }

        List<ClassScheduleSlot> slots = toSlots(request.getSlots());
        Map<DayOfWeek, ClassScheduleSlot> slotByDay = toSlotMap(slots);
        SchedulePlan plan = buildSchedulePlan(request.getStartDate(), courseClass.getCourse().getTotalSessions(), slotByDay, schedule);

        schedule.setSlots(slots);
        schedule.setStartDate(plan.firstSessionStart.toLocalDate());
        schedule.setEndDate(plan.lastSessionEnd.toLocalDate());
        schedule.setRoom(request.getRoom());

        ClassSchedule saved = classScheduleRepository.save(schedule);

        courseClass.setStartDay(plan.firstSessionStart);
        courseClass.setEndDay(plan.lastSessionEnd);
        courseClassRepository.save(courseClass);

        ensureSessionsExist(saved, plan.sessions);
        return toResponse(saved);
    }

    @Override
    public ScheduleResponse getScheduleById(Long id) {
        ClassSchedule schedule = classScheduleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(schedule);
    }

    @Override
    public ScheduleResponse getScheduleByCourseClassId(Long courseClassId) {
        if (courseClassId == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        ClassSchedule schedule = classScheduleRepository.findByCourseClass_Id(courseClassId);
        if (schedule == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        return toResponse(schedule);
    }

    @Override
    public List<ScheduleResponse> getMySchedules() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long userId = jwt.getClaim("userId");

        Set<Long> courseClassIds = new LinkedHashSet<>();
        if (auth.getAuthorities().stream().anyMatch(a -> "ROLE_TEACHER".equals(a.getAuthority()))) {
            List<TeachingAssignment> assignments = teachingAssignmentRepository.findByTeacher_UserId(userId);
            for (TeachingAssignment assignment : assignments) {
                if (assignment.getCourseClass() != null && assignment.getCourseClass().getId() != null) {
                    courseClassIds.add(assignment.getCourseClass().getId());
                }
            }
        }
        if (auth.getAuthorities().stream().anyMatch(a -> "ROLE_STUDENT".equals(a.getAuthority()))) {
            List<Enrollment> enrollments = enrollmentRepository.findByStudent_UserId(userId);
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getCourseClass() != null && enrollment.getCourseClass().getId() != null) {
                    courseClassIds.add(enrollment.getCourseClass().getId());
                }
            }
        }
        if (courseClassIds.isEmpty()) {
            return List.of();
        }
        List<ClassSchedule> schedules = classScheduleRepository.findByCourseClass_IdIn(new ArrayList<>(courseClassIds));
        return schedules.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PageResponse<ScheduleResponse> getListSchedule(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ClassSchedule> pageResult = classScheduleRepository.findAll(pageable);
        List<ScheduleResponse> data = pageResult.getContent()
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
    public String deleteScheduleInCourseClass(Long id) {
        ClassSchedule schedule = classScheduleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        classScheduleRepository.delete(schedule);
        return "Delete schedule successful!";
    }

    private ScheduleResponse toResponse(ClassSchedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .courseClassId(schedule.getCourseClass() != null ? schedule.getCourseClass().getId() : null)
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .slots(toSlotResponses(schedule.getSlots()))
                .room(schedule.getRoom())
                .build();
    }

    private SchedulePlan buildSchedulePlan(LocalDate startDate, Integer totalSessions, Map<DayOfWeek, ClassScheduleSlot> slotByDay, ClassSchedule schedule) {
        if (totalSessions == null || totalSessions <= 0) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        if (slotByDay.isEmpty()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        int count = 0;
        LocalDate cursor = startDate;
        LocalDateTime firstSessionStart = null;
        LocalDateTime lastSessionEnd = null;
        List<ClassSession> sessions = new ArrayList<>();
        while (true) {
            ClassScheduleSlot slot = slotByDay.get(cursor.getDayOfWeek());
            if (slot != null) {
                count++;
                ClassSession session = new ClassSession();
                session.setDate(cursor);
                session.setStartTime(slot.getStartTime());
                session.setEndTime(slot.getEndTime());
                session.setStatusClassSession(com.example.demo.constant.StatusClassSession.DOING);
                session.setClassSchedule(schedule);
                sessions.add(session);
                if (firstSessionStart == null) {
                    firstSessionStart = LocalDateTime.of(cursor, slot.getStartTime());
                }
                lastSessionEnd = LocalDateTime.of(cursor, slot.getEndTime());
                if (count == totalSessions) {
                    return new SchedulePlan(firstSessionStart, lastSessionEnd, sessions);
                }
            }
            cursor = cursor.plusDays(1);
        }
    }

    private void ensureSessionsExist(ClassSchedule schedule, List<ClassSession> sessions) {
        if (schedule.getClassSessions() != null && !schedule.getClassSessions().isEmpty()) {
            return;
        }
        if (!sessions.isEmpty()) {
            classSessionRepository.saveAll(sessions);
        }
    }

    private List<ClassScheduleSlot> toSlots(List<ScheduleSlotRequest> slotRequests) {
        List<ClassScheduleSlot> slots = new ArrayList<>();
        for (ScheduleSlotRequest request : slotRequests) {
            if (request == null || request.getDayOfWeek() == null || request.getStartTime() == null || request.getEndTime() == null) {
                throw new AppException(ErrorCode.IMFORMATION_NULL);
            }
            if (!request.getEndTime().isAfter(request.getStartTime())) {
                throw new AppException(ErrorCode.IMFORMATION_NULL);
            }
            slots.add(new ClassScheduleSlot(request.getDayOfWeek(), request.getStartTime(), request.getEndTime()));
        }
        return slots;
    }

    private Map<DayOfWeek, ClassScheduleSlot> toSlotMap(List<ClassScheduleSlot> slots) {
        Map<DayOfWeek, ClassScheduleSlot> slotByDay = new HashMap<>();
        for (ClassScheduleSlot slot : slots) {
            if (slotByDay.putIfAbsent(slot.getDayOfWeek(), slot) != null) {
                throw new AppException(ErrorCode.IMFORMATION_NULL);
            }
        }
        return slotByDay;
    }

    private List<ScheduleSlotResponse> toSlotResponses(List<ClassScheduleSlot> slots) {
        if (slots == null) {
            return List.of();
        }
        return slots.stream()
                .map(slot -> ScheduleSlotResponse.builder()
                        .dayOfWeek(slot.getDayOfWeek())
                        .startTime(slot.getStartTime())
                        .endTime(slot.getEndTime())
                        .build())
                .toList();
    }

    private static class SchedulePlan {
        private final LocalDateTime firstSessionStart;
        private final LocalDateTime lastSessionEnd;
        private final List<ClassSession> sessions;

        private SchedulePlan(LocalDateTime firstSessionStart, LocalDateTime lastSessionEnd, List<ClassSession> sessions) {
            this.firstSessionStart = firstSessionStart;
            this.lastSessionEnd = lastSessionEnd;
            this.sessions = sessions;
        }
    }
}
