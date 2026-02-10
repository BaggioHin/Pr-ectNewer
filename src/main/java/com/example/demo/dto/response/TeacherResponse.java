package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherResponse {
    private Long userId;
    private String teacherCode;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String phone;
    private String address;
}
