package com.backend.gns.student.domain.models;

import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import com.backend.gns.user.domain.models.User;
import com.backend.gns.wallet.domain.models.Wallet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DiscriminatorValue("STUDENT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Student extends User {

  @Column(length = 50, nullable = true, unique = true)
  private String studenNumber;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "wallet_id")
  private Wallet wallet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "universite_id")
  private Universite universite;
}