package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.DocumentEtudiantRequest;
import com.backend.gns.application.dtos.responses.DocumentEtudiantResponse;
import com.backend.gns.domain.models.DocumentEtudiant;
import com.backend.gns.domain.models.InscriptionAnnuelle;
import com.backend.gns.infrastructure.repositories.InscriptionAnnuelleRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DocumentEtudiantMapper {

  private final InscriptionAnnuelleRepository inscriptionAnnuelleRepository;

  public DocumentEtudiant toEntity(DocumentEtudiantRequest request) {
    if (request == null) {
      throw new IllegalArgumentException(
          "La requête DocumentEtudiantRequest ne peut pas être nulle");
    }

    DocumentEtudiant document = new DocumentEtudiant();
    document.setTrackingId(UUID.randomUUID());
    document.setType(request.type());
    document.setCheminFichier(request.cheminFichier());
    document.setDonneesExtraites(request.donneesExtraites());

    
    if (request.inscriptionTrackingId() != null) {
      InscriptionAnnuelle inscription =
          inscriptionAnnuelleRepository
              .findByTrackingId(request.inscriptionTrackingId())
              .orElse(null); // orElse(null) because it's nullable
      document.setInscription(inscription);
    }

    return document;
  }

  public DocumentEtudiantResponse toResponse(DocumentEtudiant document) {
     if (document == null) {
      throw new IllegalArgumentException(
          "La requête DocumentEtudiantRequest ne peut pas être nulle");
    }

    return DocumentEtudiantResponse.builder()
        .trackingId(document.getTrackingId())
        .inscriptionTrackingId(document.getInscription() != null ? document.getInscription().getTrackingId() : null) 
        .type(document.getType())
        .cheminFichier(document.getCheminFichier())
        .statut(document.getStatut())
        .commentaireRejet(document.getCommentaireRejet())
        .dateDepot(document.getDateDepot())
        .dateValidation(document.getDateValidation())
        .donneesExtraites(document.getDonneesExtraites())
        .build();
  }
}
