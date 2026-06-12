package com.backend.gns.commerce.domain.models;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.enums.StatutDocument;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DOCUMENT_MERCHANT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentMerchant extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, updatable = false)
  private UUID trackingId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "merchant_id", nullable = false)
  private Merchant merchant;

  @Enumerated(EnumType.STRING)
  @Column(length = 30, nullable = false)
  private TypeDocument type;

  @Column(length = 500, nullable = false)
  private String urlFichier;

  @Column(length = 100)
  private String publicIdCloudinary;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private StatutDocument statut;

  @Column(nullable = false)
  private LocalDateTime dateDepot;
}
