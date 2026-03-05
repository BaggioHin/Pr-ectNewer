package com.example.demo.service.k1;

import com.example.demo.dto.request.ScheduleRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ScheduleResponse;

import java.util.List;

public interface ScheduleService {
    ScheduleResponse addScheduleInCourseClass(ScheduleRequest request);

    ScheduleResponse getScheduleById(Long id);

    ScheduleResponse getScheduleByCourseClassId(Long courseClassId);

    List<ScheduleResponse> getMySchedules();

    PageResponse<ScheduleResponse> getListSchedule(int page, int size);

    String deleteScheduleInCourseClass(Long id);
}
