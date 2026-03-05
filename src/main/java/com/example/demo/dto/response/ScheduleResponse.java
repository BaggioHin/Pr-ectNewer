package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponse {
    private Long id;
    private Long courseClassId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<ScheduleSlotResponse> slots;
    private String room;
}
