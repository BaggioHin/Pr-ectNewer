package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class CloudinaryUploadResult {
    private final String url;
    private final String publicId;
}
