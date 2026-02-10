package com.example.demo.entity.Document;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;



@Entity
@Table(name = "document_embedding")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @NotNull
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding", columnDefinition = "vector(1536)", nullable = false)
    private float[] embedding;

    @Column(name = "content_chunk", columnDefinition = "TEXT")
    private String contentChunk;
}


