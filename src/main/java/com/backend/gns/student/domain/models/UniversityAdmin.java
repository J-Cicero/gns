package com.backend.gns.student.domain.models;

import com.backend.gns.user.domain.models.User;
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
@DiscriminatorValue("UNIVERSITY_ADMIN")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UniversityAdmin extends User {

  @Column(unique = true, nullable = true)
  private String numeroCompte;

  @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", unique = true)
  private Wallet wallet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "universite_id")
  private Universite universite;
}
