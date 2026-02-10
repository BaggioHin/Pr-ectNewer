package com.example.demo.repository;

import com.example.demo.entity.gradeAndEvaluate.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByQuestion_Id(Long questionId, Pageable pageable);
}
