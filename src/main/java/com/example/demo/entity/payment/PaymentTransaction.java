package com.example.demo.entity.payment;

import com.example.demo.constant.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
        name = "payment_transactions",
        indexes = {
                @Index(name = "idx_payment_txnref", columnList = "txn_ref"),
                @Index(name = "idx_payment_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "txn_ref", nullable = false, unique = true, length = 64)
    private String txnRef;

    @NotNull
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private PaymentStatus status;

    @Column(name = "course_class_id", nullable = false)
    private Long courseClassId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "saler_user_id", nullable = false)
    private Long salerUserId;

    @Column(name = "order_info", length = 500)
    private String orderInfo;

    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @Column(name = "payment_instructions", columnDefinition = "TEXT")
    private String paymentInstructions;

    @Column(name = "confirmation_code", length = 64)
    private String confirmationCode;

    @Column(name = "confirmed_by")
    private Long confirmedBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "vnp_response_code", length = 10)
    private String vnpResponseCode;

    @Column(name = "vnp_transaction_status", length = 10)
    private String vnpTransactionStatus;

    @Column(name = "vnp_transaction_no", length = 50)
    private String vnpTransactionNo;

    @Column(name = "vnp_pay_date", length = 20)
    private String vnpPayDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
