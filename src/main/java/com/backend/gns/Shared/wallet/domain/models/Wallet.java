package com.backend.gns.Shared.wallet.domain.models;

import com.backend.gns.Shared.utils.BaseEntity;
import com.backend.gns.Shared.wallet.domain.enums.WalletStatus;
import com.backend.gns.Shared.wallet.domain.enums.WalletType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "wallet_owner_type", discriminatorType = DiscriminatorType.STRING)
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

  @Column(nullable = false)
  private BigDecimal solde;

  @Column(nullable = false)
  private BigDecimal plafond;

  @Column
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime dateCreation;

}
