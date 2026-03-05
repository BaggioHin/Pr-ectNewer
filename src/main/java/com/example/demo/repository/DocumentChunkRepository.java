package com.example.demo.repository;

import com.example.demo.entity.Document.DocumentChunk;
import com.example.demo.repository.projection.DocumentEmbeddingSearchRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    void deleteByDocument_Id(Long documentId);

    @Query(
            value = """
                    SELECT
                        dc.document_id AS documentId,
                        dc.content AS contentChunk,
                        ts_rank(
                            setweight(to_tsvector('simple', coalesce(d.title, '')), 'A') ||
                            setweight(to_tsvector('simple', coalesce(d.description, '')), 'B') ||
                            setweight(to_tsvector('simple', dc.content), 'C'),
                            plainto_tsquery('simple', :query)
                        ) AS score
                    FROM document_chunk dc
                    JOIN document d ON d.id = dc.document_id
                    WHERE
                        to_tsvector('simple', coalesce(d.title, '') || ' ' || coalesce(d.description, '')) @@ plainto_tsquery('simple', :query)
                        OR to_tsvector('simple', dc.content) @@ plainto_tsquery('simple', :query)
                    ORDER BY score DESC
                    LIMIT :limit
                    """,
            nativeQuery = true
    )
    List<DocumentEmbeddingSearchRow> searchFullText(
            @Param("query") String query,
            @Param("limit") int limit
    );
}
