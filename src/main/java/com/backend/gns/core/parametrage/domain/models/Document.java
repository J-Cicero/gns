package com.backend.gns.core.parametrage.domain.models;

import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "document_type_disc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(of = "trackingId")
public abstract class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID trackingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type_field", length = 30, nullable = false)
    private TypeDocument documentType;

    @Column(length = 500, nullable = false)
    private String fileUrl;

    @Column(length = 100)
    private String providerPublicId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatutDocument status = StatutDocument.EN_ATTENTE;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(length = 500)
    private String rejectionReason;

    // NE RIEN AJOUTER ICI (pas de equals, pas de hashCode)
    // Laisse Lombok gérer les constructeurs via @SuperBuilder
}