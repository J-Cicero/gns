package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.InscriptionAnnuelleRequest;
import com.backend.gns.student.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
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
    inscription.setNiveau(request.niveau());

    if (request.studentTrackingId() != null) {
      Student student =
          studentRepository
              .findByTrackingId(request.studentTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Étudiant non trouvé avec trackingId: " + request.studentTrackingId()));
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
        .studentTrackingId(
            inscription.getStudent() != null ? inscription.getStudent().getTrackingId() : null)
        .studentNom(inscription.getStudent() != null ? inscription.getStudent().getNom() : null)
        .studentPrenom(
            inscription.getStudent() != null ? inscription.getStudent().getPrenom() : null)
        .numEtudiantUniv(
            inscription.getStudent() != null ? inscription.getStudent().getMatricule() : null)
        .anneeAcademique(
            inscription.getScolariteYear() != null
                ? inscription.getScolariteYear().getLibelle()
                : null)
        .niveau(inscription.getNiveau())
        .estInscritDefinitif(inscription.isEstInscritDefinitif())
        .estEligibleBourse(inscription.isEstEligibleBourse())
        .typeBourse(inscription.getTypeBourse())
        .dateValidationApi(inscription.getDateValidationApi())
        .plafondAccorde(inscription.getPlafondAccorde())
        .build();
  }
}
