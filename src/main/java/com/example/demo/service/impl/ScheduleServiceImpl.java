package com.example.demo.service.impl;

import com.example.demo.dto.request.ScheduleRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ScheduleResponse;
import com.example.demo.entity.classAndLearn.ClassSchedule;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.ClassScheduleRepository;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.service.k1.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    CourseClassRepository courseClassRepository;
    @Autowired
    ClassScheduleRepository classScheduleRepository;

    @Override
    public ScheduleResponse addScheduleInCourseClass(ScheduleRequest request) {
        if (request == null || request.getCourseClassId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        CourseClass courseClass = courseClassRepository.findById(request.getCourseClassId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));

        ClassSchedule schedule = courseClass.getSchedule();
        if (schedule == null) {
            schedule = new ClassSchedule();
            schedule.setCourseClass(courseClass);
        }

        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setRoom(request.getRoom());

        ClassSchedule saved = classScheduleRepository.save(schedule);
        return toResponse(saved);
    }

    @Override
    public ScheduleResponse getScheduleById(Long id) {
        ClassSchedule schedule = classScheduleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(schedule);
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
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .room(schedule.getRoom())
                .build();
    }
}
