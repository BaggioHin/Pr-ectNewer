package com.example.demo.dto.response;

import com.example.demo.constant.TypeGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEventResponse {
    private String type; // CLASS_SESSION or EXAM_DEADLINE
    private Long courseClassId;
    private String courseClassName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String title;
    private Long refId;
    private String subjectName;
    private TypeGrade typeGrade;
}
