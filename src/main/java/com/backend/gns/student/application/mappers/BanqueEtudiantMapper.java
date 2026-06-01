package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.BanqueEtudiantRequest;
import com.backend.gns.student.application.dtos.responses.BanqueEtudiantResponse;
import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.core.domain.models.Banque;
import com.backend.gns.core.infrastructure.repositories.BanqueRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class BanqueEtudiantMapper {

  private final StudentRepository studentRepository;
  private final BanqueRepository banqueRepository;

  public BanqueEtudiantMapper(StudentRepository studentRepository, BanqueRepository banqueRepository) {
    this.studentRepository = studentRepository;
    this.banqueRepository = banqueRepository;
  }

  public BanqueEtudiant toEntity(BanqueEtudiantRequest request) {

    if (request == null) {
      throw new IllegalArgumentException("La requête BanqueEtudiantRequest ne peut pas être nulle");
    }

    BanqueEtudiant banqueEtudiant = new BanqueEtudiant();
    banqueEtudiant.setTrackingId(UUID.randomUUID());
    if (request.banqueId() != null) {
      Banque banque = banqueRepository.findByTrackingId(request.banqueId())
          .orElseThrow(() -> new IllegalArgumentException("Banque introuvable"));
      banqueEtudiant.setBanque(banque);
    }
    banqueEtudiant.setRIB(request.RIB());
    banqueEtudiant.setMandatStatut(request.mandatStatut());
    banqueEtudiant.setMandatSigne(request.mandatSigne());
    banqueEtudiant.setMandatTimestamp(request.mandatTimestamp());
    banqueEtudiant.setLieuEnregistrement(request.lieuEnregistrement());
    banqueEtudiant.setMandatValideLeDate(request.mandatValideLeDate());

    if (request.studentTrackingId() != null) {
      Student student =
          studentRepository
              .findByTrackingId(request.studentTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Student not found with trackingId : " + request.studentTrackingId()));

      banqueEtudiant.setStudent(student);
    }
    return banqueEtudiant;
  }

  public BanqueEtudiantResponse toResponse(BanqueEtudiant banqueEtudiant) {
    if (banqueEtudiant == null) {
      throw new IllegalArgumentException("L'entité BanqueEtudiant ne peut pas être nulle");
    }

    return BanqueEtudiantResponse.builder()
        .trackingId(banqueEtudiant.getTrackingId())
        .studentTrackingId(
            banqueEtudiant.getStudent() != null
                ? banqueEtudiant.getStudent().getTrackingId()
                : null)
        .banqueId(banqueEtudiant.getBanque() != null ? banqueEtudiant.getBanque().getTrackingId() : null)
        .banqueName(banqueEtudiant.getBanque() != null ? banqueEtudiant.getBanque().getNom() : null)
        .RIB(banqueEtudiant.getRIB())
        .mandatStatut(banqueEtudiant.getMandatStatut())
        .mandatSigne(banqueEtudiant.isMandatSigne())
        .mandatTimestamp(banqueEtudiant.getMandatTimestamp())
        .lieuEnregistrement(banqueEtudiant.getLieuEnregistrement())
        .mandatValideLeDate(banqueEtudiant.getMandatValideLeDate())
        .build();
  }

  public BanqueEtudiant toEntityFromResponse(BanqueEtudiantResponse response) {

    if (response == null) {
      throw new IllegalArgumentException(
          "La réponse BanqueEtudiantResponse ne peut pas être nulle");
    }

    BanqueEtudiant banqueEtudiant = new BanqueEtudiant();
    banqueEtudiant.setTrackingId(response.trackingId());
    
    if (response.banqueId() != null) {
      Banque banque = banqueRepository.findByTrackingId(response.banqueId())
          .orElseThrow(() -> new IllegalArgumentException("Banque introuvable"));
      banqueEtudiant.setBanque(banque);
    }
    
    banqueEtudiant.setRIB(response.RIB());
    banqueEtudiant.setMandatStatut(response.mandatStatut());
    banqueEtudiant.setMandatSigne(response.mandatSigne());
    banqueEtudiant.setMandatTimestamp(response.mandatTimestamp());
    banqueEtudiant.setLieuEnregistrement(response.lieuEnregistrement());

    banqueEtudiant.setMandatValideLeDate(response.mandatValideLeDate());

    if (response.studentTrackingId() != null) {
      Student student =
          studentRepository
              .findByTrackingId(response.studentTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Student not found with trackingId : " + response.studentTrackingId()));

      banqueEtudiant.setStudent(student);
    }

    return banqueEtudiant;
  }
}
