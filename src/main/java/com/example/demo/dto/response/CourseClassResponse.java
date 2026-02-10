package com.example.demo.dto.response;

import com.example.demo.constant.StatusCourse;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseClassResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private StatusCourse statusCourse;
}
