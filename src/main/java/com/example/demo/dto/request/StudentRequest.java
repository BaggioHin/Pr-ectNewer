package com.example.demo.dto.request;

import java.time.LocalDate;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class StudentRequest {
    private String fullname;
    private LocalDate dob;
    private String email;
    private String phone;
    private String thumbnailUrl;
}
