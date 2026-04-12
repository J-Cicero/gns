package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.domain.dtos.requests.StudentRequest;
import com.backend.gns.domain.dtos.requests.WalletRequest;
import com.backend.gns.domain.dtos.responses.StudentResponse;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.domain.enums.StudentNiveau;
import com.backend.gns.domain.mappers.StudentMapper;
import com.backend.gns.domain.models.Student;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import com.backend.gns.domain.services.StudentService;
import com.backend.gns.domain.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final WalletService walletService;

    @Override
    public StudentResponse create(StudentRequest request) {
        Student student = studentMapper.toEntity(request);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toResponse(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAll() {
        List<Student> students = studentRepository.findAll();
        return studentMapper.toResponseList(students);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getByTrackingId(UUID trackingId) {
        Student student = studentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with trackingId: " + trackingId));
        return studentMapper.toResponse(student);
    }

    @Override
    public StudentResponse update(UUID trackingId, StudentRequest request) {
        Student student = studentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with trackingId: " + trackingId));

        student.setNom(request.nom());
        student.setPrenom(request.prenom());
        student.setEmail(request.email());
        student.setMotDePasse(request.motDePasse());
        student.setTelephone(request.telephone());

        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toResponse(updatedStudent);
    }

    @Override
    public void delete(UUID trackingId) {
        Student student = studentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with trackingId: " + trackingId));
        studentRepository.delete(student);
    }

    @Override
    public StudentResponse validerKYC(UUID studentTrackingId) {
        // F1 - Recupere l'etudiant par trackingId
        Student student = studentRepository.findByTrackingId(studentTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentTrackingId));

        // Passe le statutKYC a VALIDE
        student.setStatutKYC(KycStatus.VALIDE);

        // Cree toujours un wallet RELAIS avec plafond standard
        WalletRequest relaisRequest = new WalletRequest(studentTrackingId, "RELAIS", 0.0, 50000.0);
        walletService.create(relaisRequest);

        // Si boursier (mandatSigne = true) ET niveau eligible -> cree HORIZON
        if (student.getMandatSigne() != null && student.getMandatSigne()) {
            boolean niveauEligible = student.getNiveau() == StudentNiveau.L1 ||
                                     student.getNiveau() == StudentNiveau.L2 ||
                                     student.getNiveau() == StudentNiveau.L3 ||
                                     student.getNiveau() == StudentNiveau.M1 ||
                                     student.getNiveau() == StudentNiveau.M2;

            if (niveauEligible) {
                WalletRequest horizonRequest = new WalletRequest(studentTrackingId, "HORIZON", 0.0, 100000.0);
                var horizonResponse = walletService.create(horizonRequest);
                
                // Si L1 -> verrouille le wallet HORIZON
                if (student.getNiveau() == StudentNiveau.L1) {
                    walletService.verrouillerWallet(horizonResponse.trackingId());
                }
            }
        }

        // Sauvegarde l'etudiant avec le nouveau statut KYC
        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toResponse(updatedStudent);
    }
}
