package com.backend.gns.core.domain.models;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "DOCUMENT_REQUIS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequis extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, updatable = false)
  private UUID trackingId = UUID.randomUUID();

  @Column(nullable = false)
  private String niveau;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TargetType targetType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TypeDocument typeDocument;

  @Column(nullable = false)
  private boolean obligatoire = true;

  @Column(nullable = false)
  private boolean estActif = true;
}
