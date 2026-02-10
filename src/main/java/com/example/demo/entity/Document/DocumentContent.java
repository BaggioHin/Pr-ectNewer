package com.example.demo.entity.Document;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "document_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    @NotNull
    private Long documentId;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String content;

    @Column(name = "chunk_index")
    @Min(0)
    private Integer chunkIndex;
}

