package com.example.demo.repository;

import com.example.demo.entity.Document.DocumentContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentContentRepository extends JpaRepository<DocumentContent, Long> {
    List<DocumentContent> findByDocumentIdOrderByChunkIndexAsc(Long documentId);
    void deleteByDocumentId(Long documentId);
}
