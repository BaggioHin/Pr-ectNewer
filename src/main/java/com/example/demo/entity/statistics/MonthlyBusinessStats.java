package com.example.demo.entity.statistics;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "monthly_business_stats",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"stat_year", "stat_month"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyBusinessStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "stat_year")
    private Integer statYear;

    @NotNull
    @Min(1)
    @Max(12)
    @Column(name = "stat_month")
    private Integer statMonth;

    @NotNull
    private Long totalRevenue;

    @NotNull
    private Long newStudentCount;

    @NotNull
    private Double closeRate;

    @NotNull
    private Double postponeRate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
