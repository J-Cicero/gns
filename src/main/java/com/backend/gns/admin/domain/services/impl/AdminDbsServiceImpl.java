package com.backend.gns.admin.domain.services.impl;

import com.backend.gns.admin.application.dtos.responses.StudentDbsStatsResponse;
import com.backend.gns.admin.domain.services.AdminDbsService;
import com.backend.gns.paiement.domain.enums.PaiementStatut;
import com.backend.gns.paiement.domain.enums.PaiementType;
import com.backend.gns.paiement.domain.models.Paiement;
import com.backend.gns.paiement.infrastructure.repositories.PaiementRepository;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDbsServiceImpl implements AdminDbsService {

  private final StudentRepository studentRepository;
  private final InscriptionAnnuelleRepository inscriptionAnnuelleRepository;
  private final PaiementRepository paiementRepository;

  @Override
  public Page<StudentDbsStatsResponse> getStudentStats(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Student> students = studentRepository.findAll(pageable);

    return students.map(this::mapToStudentDbsStatsResponse);
  }

  private StudentDbsStatsResponse mapToStudentDbsStatsResponse(Student student) {
    String typeBourse = "Aucune";
    
    Optional<InscriptionAnnuelle> activeInscription =
        inscriptionAnnuelleRepository.findByStudentAndScolariteYear_EstOuverteTrue(student);
    
    if (activeInscription.isPresent() && activeInscription.get().getTypeBourse() != null) {
      typeBourse = activeInscription.get().getTypeBourse().name();
    }

    List<Paiement> achats = paiementRepository.findByStudentAndTypePaiementAndStatutPaiement(
        student, PaiementType.ACHAT, PaiementStatut.VALIDE);
    
    BigDecimal argentDepense = achats.stream()
        .map(Paiement::getMontantDebite)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    List<Paiement> scolarites = paiementRepository.findByStudentAndTypePaiementAndStatutPaiement(
        student, PaiementType.SCOLARITE, PaiementStatut.VALIDE);

    boolean paiementScolariteFait = !scolarites.isEmpty();
    BigDecimal montantScolarite = paiementScolariteFait 
        ? scolarites.get(0).getMontantDebite() 
        : BigDecimal.ZERO;

    return StudentDbsStatsResponse.builder()
        .trackingId(student.getTrackingId().toString())
        .nom(student.getNom())
        .prenom(student.getPrenom())
        .numEtudiantUniv(student.getNumEtudiantUniv())
        .universiteNom(student.getUniversite() != null ? student.getUniversite().getNom() : null)
        .typeBourse(typeBourse)
        .argentDepense(argentDepense)
        .paiementScolariteFait(paiementScolariteFait)
        .montantScolarite(montantScolarite)
        .build();
  }
}
