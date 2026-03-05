package com.example.demo.dto.request;

import com.example.demo.constant.ConsultationSource;
import com.example.demo.constant.ConsultationTypeReceive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationRequestCreateRequest {
    private String phone;
    private String email;
    private String message;
    private ConsultationSource source;
    private ConsultationTypeReceive typeReceive;
}
