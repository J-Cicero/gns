package com.backend.gns.user.domain.models;

import com.backend.gns.core.domain.models.Banque;
import com.backend.gns.wallet.domain.models.Wallet;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DiscriminatorValue("BANK_OPERATOR")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BankOperator extends User {

  @Column(nullable = true, length = 100)
  private String nomBanque;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "banque_partenaire_id", nullable = true)
  private Banque banquePartenaire;

  @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", unique = true)
  private Wallet wallet;
}
