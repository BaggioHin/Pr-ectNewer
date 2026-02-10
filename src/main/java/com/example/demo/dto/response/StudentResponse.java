package com.example.demo.dto.response;

import java.time.LocalDate;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long userId;
    private String fullname;
    private LocalDate dob;
    private String email;
    private String phone;
    private String thumbnailUrl;
}
