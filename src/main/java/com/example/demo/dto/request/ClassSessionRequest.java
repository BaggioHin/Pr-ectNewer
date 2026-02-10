package com.example.demo.dto.request;

import com.example.demo.constant.StatusClassSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSessionRequest {
    private Long classScheduleId;
    private LocalDate date;
    private String topic;
    private StatusClassSession statusClassSession;
}
