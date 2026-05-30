package com.backend.gns.student.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.core.domain.enums.Banque;
import com.backend.gns.student.domain.enums.MandatStatut; 
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "BANQUE_ETUDIANT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BanqueEtudiant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID trackingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Banque banque;

    @Column(unique = true, nullable = true)
    private String RIB;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean mandatSigne = false;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime mandatTimestamp;

    @Column(length = 45)
    private String lieuEnregistrement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50) 
    private MandatStatut mandatStatut;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") 
    private LocalDateTime mandatValideLeDate;

    @Column(nullable = false)
    private boolean virementEffectue = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;
}
