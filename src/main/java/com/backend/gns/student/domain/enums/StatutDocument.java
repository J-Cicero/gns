package com.backend.gns.student.domain.enums;

public enum StatutDocument {
    EN_ATTENTE, // document soumis mais pas encore traité
    VALIDE, // document vérifié et accepté
    REJETE,  // document vérifié et refusé (ex: non conforme, illisible)
}
