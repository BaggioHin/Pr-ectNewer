package com.example.demo.service.k1;

import com.example.demo.dto.request.AttendanceRequest;
import com.example.demo.dto.response.AttendanceResponse;
import com.example.demo.dto.response.PageResponse;

public interface AttendanceService {
    AttendanceResponse getAttendanceById(Long classSessionId, Long studentId);

    PageResponse<AttendanceResponse> getListAttendance(int page, int size);

    AttendanceResponse addAttendance(AttendanceRequest request);

    AttendanceResponse editAttendance(Long classSessionId, Long studentId, AttendanceRequest request);

    String deleteAttendance(Long classSessionId, Long studentId);
}
