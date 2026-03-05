package com.example.demo.dto.response;

import com.example.demo.constant.StatusCourse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer totalSessions;
    private StatusCourse statusCourse;
    private Long credit;
}
