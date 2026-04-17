package com.backend.gns.domain.models;

import com.backend.gns.Shared.utils.BaseEntity;
import com.backend.gns.domain.enums.KycStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "BOUTIQUE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Boutique extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 36, nullable = false, unique = true, updatable = false)
  private UUID trackingId;

  @Column(length = 100, nullable = true)
  private String nomBoutique;

  @Column(length = 100)
  private String categorieShop;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private KycStatus statutKYC;

  @Column private Double latitude;

  @Column private Double longitude;

  @ManyToOne
  @JoinColumn(name = "merchant_id", nullable = false)
  private Merchant merchant;

  @OneToOne
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet;
}
