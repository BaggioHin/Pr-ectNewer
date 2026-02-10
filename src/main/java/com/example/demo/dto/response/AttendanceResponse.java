package com.example.demo.dto.response;

import com.example.demo.constant.StatusAttendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    private Long classSessionId;
    private Long studentId;
    private StatusAttendance statusAttendance;
}
