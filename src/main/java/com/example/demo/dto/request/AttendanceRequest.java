package com.example.demo.dto.request;

import com.example.demo.constant.StatusAttendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRequest {
    private Long classSessionId;
    private Long studentId;
    private StatusAttendance statusAttendance;
}
