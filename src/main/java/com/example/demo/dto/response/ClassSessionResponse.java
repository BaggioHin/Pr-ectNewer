package com.example.demo.dto.response;

import com.example.demo.constant.StatusClassSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSessionResponse {
    private Long id;
    private Long classScheduleId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String topic;
    private StatusClassSession statusClassSession;
    private boolean makeup;
    private Long makeupForSessionId;
}
