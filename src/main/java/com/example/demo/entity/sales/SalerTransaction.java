package com.example.demo.entity.sales;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "saler_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saler_id", nullable = false)
    @NotNull
    private Saler saler;

    @NotBlank
    @Size(max = 255)
    private String studentName;

    @Column(name = "student_id")
    private Long studentId;

    @NotBlank
    @Size(max = 255)
    private String courseName;

    @Column(name = "course_class_id")
    private Long courseClassId;

    @NotNull
    private Long amount;

    @NotNull
    private LocalDateTime occurredAt;
}
