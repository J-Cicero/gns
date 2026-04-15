package com.backend.gns.application.mappers;

import com.backend.gns.Shared.user.domain.enums.TypeRole;
import com.backend.gns.domain.dtos.requests.StudentRequest;
import com.backend.gns.domain.dtos.responses.StudentResponse;
import com.backend.gns.domain.models.Student;
import com.backend.gns.domain.enums.StudentNiveau;
import com.backend.gns.domain.enums.KycStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    private final PasswordEncoder passwordEncoder;

    public StudentMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Student toEntity(StudentRequest request) {
        if (request == null) {
            return null;
        }

        Student student = new Student();
        student.setTrackingId(UUID.randomUUID());
        student.setNom(request.nom());
        student.setPrenom(request.prenom());
        student.setEmail(request.email());
        student.setPassword(passwordEncoder.encode(request.motDePasse()));
        student.setTelephone(request.telephone());
        student.setMatriculeUL(request.matriculeUL());
        student.setNiveau(StudentNiveau.valueOf(request.niveau()));
        student.setMentionBac(request.mentionBac());
        student.setCreditsValides(request.creditsValides());
        student.setRIB(request.RIB());
        student.setCheminCarteEtu(request.cheminCarteEtu());
        student.setCheminReleve(request.cheminReleve());
        student.setMandatSigne(request.mandatSigne());
        if (request.dateMandatSigne() != null) {
            student.setDateMandatSigne(LocalDate.parse(request.dateMandatSigne()));
        }
        student.setStatutKYC(KycStatus.EN_ATTENTE);
        student.setRole(TypeRole.ETUDIANT);
        student.setEstActif(false);

        return student;
    }

    public StudentResponse toResponse(Student entity) {
        if (entity == null) {
            return null;
        }

        return new StudentResponse(
                entity.getTrackingId(),
                entity.getNom(),
                entity.getPrenom(),
                entity.getEmail(),
                entity.getTelephone(),
                entity.getDateInscription(),
                entity.isEstActif(),
                entity.getMatriculeUL(),
                entity.getNiveau() != null ? entity.getNiveau().name() : null,
                entity.getMentionBac(),
                entity.getCreditsValides(),
                entity.getRIB(),
                entity.getCheminCarteEtu(),
                entity.getCheminReleve(),
                entity.getMandatSigne(),
                entity.getDateMandatSigne(),
                entity.getStatutKYC() != null ? entity.getStatutKYC().name() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<StudentResponse> toResponseList(List<Student> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
