package com.example.demo.dto.request;

import com.example.demo.constant.StatusCourse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequest {
    private String name;
    private String description;
    private Integer totalSessions;
    private StatusCourse statusCourse;
    private Long price;
}
