package com.example.demo.entity.gradeAndEvaluate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    @NotNull
    private Exam exam;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String questionText;

    @Column(name = "option_a", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String optionA;

    @Column(name = "option_b", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String optionB;

    @Column(name = "option_c", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String optionC;

    @Column(name = "option_d", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String optionD;

    @Column(name = "correct_option", nullable = false, length = 1)
    @NotBlank
    @Size(max = 1)
    @Pattern(regexp = "[ABCD]")
    private String correctOption;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}
