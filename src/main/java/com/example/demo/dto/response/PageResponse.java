package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> data;

    private int page;          // page hiện tại
    private int size;          // size mỗi page
    private long totalElements;
    private int totalPages;
}

