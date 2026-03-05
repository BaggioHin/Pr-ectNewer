package com.example.demo.entity.Document;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "document_chunk",
        indexes = {
                @Index(name = "idx_document_id", columnList = "document_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"document_id", "chunk_index"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentChunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

}
