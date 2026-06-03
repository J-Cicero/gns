package com.backend.gns.student.domain.models;

import com.backend.gns.core.domain.models.Banque;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "COMPTE_BANCAIRE_UNIVERSITE",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"universite_id", "banque_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompteBancaireUniversite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, updatable = false)
  private UUID trackingId = UUID.randomUUID();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "universite_id", nullable = false)
  private Universite universite;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "banque_id", nullable = false)
  private Banque banque;

  @Column(name = "numero_compte", length = 30, nullable = false)
  private String numeroCompte;
}
