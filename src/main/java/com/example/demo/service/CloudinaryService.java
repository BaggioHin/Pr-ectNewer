package com.example.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.example.demo.dto.request.CloudinaryUploadResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public CloudinaryUploadResult uploadAvatar(MultipartFile file, String userId) {
        try {
            String publicId = "avatars/avatar_" + userId;

            Map<String, Object> options = new HashMap<>();
            options.put("public_id", publicId);
            options.put("overwrite", true);
            options.put("resource_type", "image");
            options.put("transformation", new Transformation()
                    .width(300)
                    .height(300)
                    .crop("fill"));

            Map<?, ?> uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(), options);

            String secureUrl = uploadResult.get("secure_url").toString();

            return new CloudinaryUploadResult(secureUrl, publicId);

        } catch (IOException e) {
            throw new RuntimeException("Upload avatar failed", e);
        }
    }

    public CloudinaryUploadResult uploadDocument(MultipartFile file, String userId) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileName = originalFilename == null ? "document" : originalFilename;
            int dotIndex = fileName.lastIndexOf('.');
            String extension = dotIndex >= 0 ? fileName.substring(dotIndex + 1) : "pdf";
            String baseName = dotIndex > 0 ? fileName.substring(0, dotIndex) : "document";
            String safeBaseName = baseName.replaceAll("[^a-zA-Z0-9_-]", "_");

            String publicId = "documents/user_" + userId + "/" + safeBaseName + "_" + UUID.randomUUID();

            Map<String, Object> options = new HashMap<>();
            options.put("public_id", publicId);
            options.put("resource_type", "raw");
            options.put("overwrite", false);
            options.put("format", extension);

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            String secureUrl = String.valueOf(uploadResult.get("secure_url"));

            return new CloudinaryUploadResult(secureUrl, publicId);
        } catch (IOException e) {
            throw new RuntimeException("Upload document failed", e);
        }
    }

    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            Map<String, Object> options = new HashMap<>();
            options.put("resource_type", "image");
            options.put("invalidate", true);

            Map result = cloudinary.uploader().destroy(publicId, options);
            String status = String.valueOf(result.get("result"));

            if (!"ok".equalsIgnoreCase(status) && !"not found".equalsIgnoreCase(status)) {
                throw new RuntimeException("Delete avatar failed: " + status);
            }
        } catch (IOException e) {
            throw new RuntimeException("Delete avatar failed", e);
        }
    }

    public void deleteDocument(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            Map<String, Object> options = new HashMap<>();
            options.put("resource_type", "raw");
            options.put("invalidate", true);

            Map result = cloudinary.uploader().destroy(publicId, options);
            String status = String.valueOf(result.get("result"));

            if (!"ok".equalsIgnoreCase(status) && !"not found".equalsIgnoreCase(status)) {
                throw new RuntimeException("Delete document failed: " + status);
            }
        } catch (IOException e) {
            throw new RuntimeException("Delete document failed", e);
        }
    }
}
