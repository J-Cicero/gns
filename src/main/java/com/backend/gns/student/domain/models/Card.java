package com.backend.gns.student.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.enums.CardStatut;
import com.backend.gns.wallet.domain.models.Wallet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "CARD")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Card extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, updatable = false)
  private UUID trackingId;

  @Column(unique = true, nullable = false)
  private String cardNumber;

  @Column(unique = true, nullable = false)
  private String qrCodeData;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private CardStatut status;

  @Column 
  private LocalDateTime emissionDate;

  @Column
  private LocalDateTime expirationDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet;
}
