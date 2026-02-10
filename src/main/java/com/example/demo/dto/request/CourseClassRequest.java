package com.example.demo.dto.request;

import com.example.demo.constant.StatusCourse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseClassRequest {
//    private Long id;
    private String code;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private StatusCourse statusCourse;
    private Long courseId;
}
