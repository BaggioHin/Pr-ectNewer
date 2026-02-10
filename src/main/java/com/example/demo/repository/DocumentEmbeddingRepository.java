package com.example.demo.repository;

import com.example.demo.entity.Document.DocumentEmbedding;
import com.example.demo.repository.projection.DocumentEmbeddingSearchRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentEmbeddingRepository extends JpaRepository<DocumentEmbedding, Long> {

    @Query(
            value = """
                    SELECT
                        document_id AS documentId,
                        content_chunk AS contentChunk,
                        1 - (embedding <=> :queryEmbedding) AS score
                    FROM document_embedding
                    ORDER BY embedding <=> :queryEmbedding
                    LIMIT :limit
                    """,
            nativeQuery = true
    )
    List<DocumentEmbeddingSearchRow> searchSimilar(
            @Param("queryEmbedding") float[] queryEmbedding,
            @Param("limit") int limit
    );

    void deleteByDocumentId(Long documentId);
}
