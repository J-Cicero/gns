package com.backend.gns.student.application.mappers;

import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import com.backend.gns.student.application.dtos.requests.StudentRequest;
import com.backend.gns.student.application.dtos.responses.StudentResponse;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.models.Universite;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import com.backend.gns.user.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@AllArgsConstructor
public class StudentMapper {

  private final UniversiteRepository universiteRepository;
  // NB: J'ai retiré WalletRepository car le Wallet est généré automatiquement et non récupéré depuis la requête.

  public Student toEntity(StudentRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("StudentRequest cannot be null");
    }

    Student student = new Student();
    student.setTrackingId(UUID.randomUUID());

    student.setEmail(request.email());
    student.setPassword(request.password());
    student.setLastName(request.lastName());
    student.setFirstName(request.firstName());
    student.setRole(UserRole.ETUDIANT);
    student.setPhoneNumber(request.phoneNumber());

    student.setActive(false);
    student.setKycStatus(KycStatus.EN_ATTENTE);

    student.setBirthDate(request.birthDate());
    student.setBirthPlace(request.birthPlace());
    student.setStudenNumber(request.studentNumber());

    // Gestion de l'
    if (request.universiteTrackingId() != null) {
      Universite universite = universiteRepository
              .findByTrackingId(request.universiteTrackingId())
              .orElseThrow(() -> new IllegalArgumentException("University not found with trackingId: " + request.universiteTrackingId()));
      student.setUniversite(universite);
    }

    return student;
  }
  public StudentResponse toResponse(Student entity) {
    if (entity == null) {
      return null;
    }

    return StudentResponse.builder()
            .trackingId(entity.getTrackingId())
            .email(entity.getEmail())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .phoneNumber(entity.getPhoneNumber())

            .isActive(entity.isActive())
            .kycStatus(entity.getKycStatus())

            .birthDate(entity.getBirthDate())
            .birthPlace(entity.getBirthPlace())
            .studentNumber(entity.getStudenNumber())

            .universiteTrackingId(entity.getUniversite() != null ? entity.getUniversite().getTrackingId() : null)
            .walletTrackingId(entity.getWallet() != null ? entity.getWallet().getTrackingId() : null)
            .build();
  }
}