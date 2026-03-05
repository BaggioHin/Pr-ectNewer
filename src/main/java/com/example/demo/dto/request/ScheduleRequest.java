package com.example.demo.dto.request;

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
public class ScheduleRequest {
    private Long courseClassId;
    private LocalDate startDate;
    private List<ScheduleSlotRequest> slots;
    private String room;
}
