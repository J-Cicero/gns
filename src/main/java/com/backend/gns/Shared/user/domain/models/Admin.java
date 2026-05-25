package com.backend.gns.Shared.user.domain.models;

import com.backend.gns.Shared.wallet.domain.models.Wallet;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Admin extends User {

  @Column(length = 20, nullable = true)
  private String numeroCompte;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", unique = true)
  private Wallet wallet;
}
