package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.InscriptionAnnuelleRequest;
import com.backend.gns.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.domain.models.InscriptionAnnuelle;
import com.backend.gns.domain.models.Student;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InscriptionAnnuelleMapper {

  private final StudentRepository studentRepository;

  public InscriptionAnnuelle toEntity(InscriptionAnnuelleRequest request) {
    if (request == null) {
      throw new IllegalArgumentException(
          "La requête InscriptionAnnuelleRequest ne peut pas être nulle");
    }

    InscriptionAnnuelle inscription = new InscriptionAnnuelle();
    inscription.setTrackingId(UUID.randomUUID());
    inscription.setAnneeAcademique(request.anneeAcademique());
    inscription.setNiveau(request.niveau());
    inscription.setCreditsTotalValides(request.creditsTotalValides());
    inscription.setMentionBac(request.mentionBac());
    inscription.setEstBoursier(request.estBoursier());
    inscription.setTypeBourse(request.typeBourse());
    inscription.setFraisScolaritePayes(request.fraisScolaritePayes());
    inscription.setStatut(request.statut());
    inscription.setSource(request.source());

    if (request.studentTrackingId() != null) {
      Student student =
          studentRepository
              .findByTrackingId(request.studentTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Étudiant non trouvé avec trackingId: "
                              + request.studentTrackingId()));
      inscription.setStudent(student);
    }

    return inscription;
  }

  public InscriptionAnnuelleResponse toResponse(InscriptionAnnuelle inscription) {
    if (inscription == null) {
      return null;
    }

    return InscriptionAnnuelleResponse.builder()
        .trackingId(inscription.getTrackingId())
        .studentTrackingId(inscription.getStudent().getTrackingId())
        .anneeAcademique(inscription.getAnneeAcademique())
        .niveau(inscription.getNiveau())
        .creditsTotalValides(inscription.getCreditsTotalValides())
        .mentionBac(inscription.getMentionBac())
        .estBoursier(inscription.isEstBoursier())
        .typeBourse(inscription.getTypeBourse())
        .fraisScolaritePayes(inscription.isFraisScolaritePayes())
        .statut(inscription.getStatut())
        .source(inscription.getSource())
        .dateActivation(inscription.getDateActivation())
        .build();
  }
}
