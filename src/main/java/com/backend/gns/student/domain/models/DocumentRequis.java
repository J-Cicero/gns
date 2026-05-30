package com.backend.gns.student.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.core.domain.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.*;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentNiveau niveau;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocument typeDocument;

    @Column(nullable = false)
    private boolean obligatoire = true;

    @Column(nullable = false)
    private boolean estActif = true;
}
