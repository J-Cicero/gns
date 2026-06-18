package com.backend.gns.student.domain.models;

import com.backend.gns.core.parametrage.domain.models.Document;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DOCUMENT_ETUDIANT")
@Getter
@Setter
@NoArgsConstructor
public class DocumentEtudiant extends Document { // Extends generic Document
  // No additional fields needed here, as all relevant fields are in the generic Document.
  // The 'ownerType' will be implicitly STUDENT when this entity is used.
}
