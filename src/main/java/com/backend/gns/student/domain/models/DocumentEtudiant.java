
package com.backend.gns.student.domain.models;

import com.backend.gns.core.parametrage.domain.models.Document;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ETUDIANT")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DocumentEtudiant extends Document {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscription_annuelle_id")
    private InscriptionAnnuelle inscription;
}