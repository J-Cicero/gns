package com.backend.gns.paiement.domain.models;

import com.backend.gns.Shared.utils.BaseEntity;
import com.backend.gns.paiement.domain.enums.CommandeStatut;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.commerce.domain.models.Boutique;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "COMMANDE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Commande extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, updatable = false)
  private UUID trackingId;

  @Column(length = 36, nullable = false, unique = true)
  private String reference;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "boutique_id", nullable = false)
  private Boutique boutique;

  @Column(nullable = false)
  private BigDecimal montantTotal;

  @Column
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime dateCommande;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private CommandeStatut statut;

}
