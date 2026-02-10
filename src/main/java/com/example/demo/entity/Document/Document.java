package com.example.demo.entity.Document;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    @Size(max = 4000)
    private String description;

    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    @Size(max = 2048)
    private String fileUrl;

    @Column(name = "file_type", length = 20)
    @Size(max = 20)
    private String fileType; // PDF, DOCX

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "uploaded_by", nullable = false)
    @NotNull
    private Long uploadedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
