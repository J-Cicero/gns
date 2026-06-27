package com.backend.gns.commerce.domain.models;

import com.backend.gns.commerce.domain.enums.LiquidationStatut;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.Student;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_liquidations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentLiquidation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID trackingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scolarite_year_id", nullable = false)
    private ScolariteYear scolariteYear;

    @Column(nullable = false)
    private BigDecimal amountDeducted;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime validatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LiquidationStatut status;

    private String transactionReference;

    @OneToMany(mappedBy = "studentLiquidation", fetch = FetchType.LAZY)
    private java.util.List<Transaction> transactions;
}
