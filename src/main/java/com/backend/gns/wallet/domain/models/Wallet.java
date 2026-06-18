package com.backend.gns.wallet.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.wallet.domain.enums.WalletFundingLevel;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "WALLET")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Wallet extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, updatable = false)
  private UUID trackingId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private WalletType walletType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private WalletStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private WalletFundingLevel fundingLevel;

  @Column(nullable = false)
  private BigDecimal balance;

  @Column(nullable = false)
  private BigDecimal limitAmount;

  @Column(length = 3)
  private String currency = "XAF";

  @Column
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private Student student;

  @PrePersist
  public void onPrePersist() {
    if (this.trackingId == null) {
      this.trackingId = UUID.randomUUID();
    }
    calculateFundingLevel();
  }

  @PreUpdate
  public void onPreUpdate() {
    calculateFundingLevel();
  }

  private void calculateFundingLevel() {
    if (balance == null || balance.compareTo(BigDecimal.ZERO) <= 0) {
      this.fundingLevel = WalletFundingLevel.EPUISE;
    } else if (limitAmount == null || limitAmount.compareTo(BigDecimal.ZERO) <= 0) {
      this.fundingLevel = WalletFundingLevel.NORMAL;
    } else {
      BigDecimal percentage = balance.divide(limitAmount, 4, java.math.RoundingMode.HALF_UP);
      if (percentage.compareTo(new BigDecimal("0.10")) <= 0) {
        this.fundingLevel = WalletFundingLevel.CRITIQUE;
      } else if (percentage.compareTo(new BigDecimal("0.30")) <= 0) {
        this.fundingLevel = WalletFundingLevel.FAIBLE;
      } else {
        this.fundingLevel = WalletFundingLevel.NORMAL;
      }
    }
  }
}
