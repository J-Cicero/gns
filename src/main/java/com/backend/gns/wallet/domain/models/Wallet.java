package com.backend.gns.wallet.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.wallet.domain.enums.WalletFundingLevel;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
  private WalletType typeWallet;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private WalletStatus statutWallet;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private WalletFundingLevel niveauSolde;

  @Column(nullable = false)
  private BigDecimal solde;

  @Column(nullable = false)
  private BigDecimal plafond;

  @Column
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime dateCreation;

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
    if (solde == null || solde.compareTo(BigDecimal.ZERO) <= 0) {
      this.niveauSolde = WalletFundingLevel.EPUISE;
    } else if (plafond == null || plafond.compareTo(BigDecimal.ZERO) <= 0) {
      this.niveauSolde = WalletFundingLevel.NORMAL;
    } else {
      BigDecimal percentage = solde.divide(plafond, 4, java.math.RoundingMode.HALF_UP);
      if (percentage.compareTo(new BigDecimal("0.10")) <= 0) {
        this.niveauSolde = WalletFundingLevel.CRITIQUE;
      } else if (percentage.compareTo(new BigDecimal("0.30")) <= 0) {
        this.niveauSolde = WalletFundingLevel.FAIBLE;
      } else {
        this.niveauSolde = WalletFundingLevel.NORMAL;
      }
    }
  }
}
