package com.backend.gns.commerce.domain.models;

import com.backend.gns.commerce.domain.enums.TransactionStatut;
import com.backend.gns.student.domain.models.Student;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID trackingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Student sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Boutique receiver;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatut status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amountDebited;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amountCredited;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalCommission;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal gnsCommission;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal bankCommission;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCommissionPaid = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRetry = false;
}
