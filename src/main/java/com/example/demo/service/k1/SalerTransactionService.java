package com.example.demo.service.k1;

import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SalerStudentTransactionResponse;

import java.util.List;

public interface SalerTransactionService {
    PageResponse<SalerStudentTransactionResponse> getStudentTransactions(Long salerId, int page, int size);
}
