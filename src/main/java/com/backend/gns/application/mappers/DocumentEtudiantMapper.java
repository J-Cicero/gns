package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.DocumentEtudiantRequest;
import com.backend.gns.application.dtos.responses.DocumentEtudiantResponse;
import com.backend.gns.domain.models.DocumentEtudiant;
import com.backend.gns.domain.models.InscriptionAnnuelle;
import com.backend.gns.domain.models.Student;
import com.backend.gns.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DocumentEtudiantMapper {

  private final StudentRepository studentRepository;
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

    if (request.studentTrackingId() != null) {
      Student student =
          studentRepository
              .findByTrackingId(request.studentTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Étudiant non trouvé avec trackingId: "
                              + request.studentTrackingId()));
      document.setStudent(student);
    }

    if (request.inscriptionTrackingId() != null) {
      InscriptionAnnuelle inscription =
          inscriptionAnnuelleRepository
              .findByTrackingId(request.inscriptionTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Inscription non trouvée avec trackingId: "
                              + request.inscriptionTrackingId()));
      document.setInscription(inscription);
    }

    return document;
  }

  public DocumentEtudiantResponse toResponse(DocumentEtudiant document) {
    if (document == null) {
      return null;
    }

    return DocumentEtudiantResponse.builder()
        .trackingId(document.getTrackingId())
        .studentTrackingId(document.getStudent().getTrackingId())
        .inscriptionTrackingId(document.getInscription().getTrackingId())
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
