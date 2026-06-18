package com.backend.gns.core.parametrage.domain.models;

import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DOCUMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID trackingId;

    @Column(nullable = false)
    private UUID ownerTrackingId; // Can be studentTrackingId or merchantTrackingId

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ProprietaireType ownerType; // STUDENT or MERCHANT

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private TypeDocument documentType;

    @Column(length = 500, nullable = false)
    private String fileUrl;

    @Column(length = 100)
    private String providerPublicId; // Public ID from Cloudinary or other provider

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatutDocument status;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;
}
