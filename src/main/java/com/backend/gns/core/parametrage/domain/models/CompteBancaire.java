package com.backend.gns.core.parametrage.domain.models;

import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.user.domain.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "COMPTE_BANCAIRE")
@NoArgsConstructor
@AllArgsConstructor
public class CompteBancaire extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, updatable = false)
  private UUID trackingId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "banque_id", nullable = false)
  private Banque bank;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User proprietaire;

  @Column(length = 50, nullable = false)
  private String accountNumber;

  @Column(length = 255)
  private String ribUrl;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private ProprietaireType ownerType;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean isMainScholarshipAccount = false;

  @Column(nullable = false)
  private boolean transferCompleted = false;
}