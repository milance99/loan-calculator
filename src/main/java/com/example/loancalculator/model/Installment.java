package com.example.loancalculator.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "installments")
public class Installment {

    @Id
    @UuidGenerator
    @Column(name = "id")
    private String id;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "payment", nullable = false)
    private BigDecimal payment;

    @Column(name = "principal", nullable = false)
    private BigDecimal principal;

    @Column(name = "interest", nullable = false)
    private BigDecimal interest;

    @Column(name = "remaining_balance", nullable = false)
    private BigDecimal remainingBalance;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "loan_id", referencedColumnName = "id")
    private Loan loan;

}
