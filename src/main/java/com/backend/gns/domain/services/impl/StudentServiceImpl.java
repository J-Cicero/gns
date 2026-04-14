package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.Shared.security.utils.SecurityUtils;
import com.backend.gns.domain.dtos.requests.StudentRequest;
import com.backend.gns.domain.dtos.requests.WalletRequest;
import com.backend.gns.domain.dtos.responses.StudentResponse;
import com.backend.gns.domain.dtos.responses.WalletResponse;
import com.backend.gns.domain.dtos.responses.CommandeHistoriqueResponse;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.domain.enums.StudentNiveau;
import com.backend.gns.domain.mappers.StudentMapper;
import com.backend.gns.domain.mappers.WalletMapper;
import com.backend.gns.domain.models.Student;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import com.backend.gns.infrastructure.repositories.CommandeRepository;
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
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final CommandeRepository commandeRepository;

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
        
        // Vérifier que l'utilisateur accède à ses propres données uniquement
        SecurityUtils.verifyResourceOwnership(trackingId);
        
        return studentMapper.toResponse(student);
    }

    @Override
    public StudentResponse update(UUID trackingId, StudentRequest request) {
        Student student = studentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with trackingId: " + trackingId));

        // Vérifier que l'utilisateur modifie ses propres données uniquement
        SecurityUtils.verifyResourceOwnership(trackingId);

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

        // Cree un unique wallet HORIZON avec plafond fixe de 36000
        WalletRequest horizonRequest = new WalletRequest(studentTrackingId, "HORIZON", 0.0, 36000.0);
        var horizonResponse = walletService.create(horizonRequest);

        // Si L1 -> verrouille le wallet HORIZON
        if (student.getNiveau() == StudentNiveau.L1) {
            walletService.verrouillerWallet(horizonResponse.trackingId());
        }

        // Sauvegarde l'etudiant avec le nouveau statut KYC
        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toResponse(updatedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getWalletOfStudent(UUID studentTrackingId) {
        // Vérifie que l'étudiant existe
        studentRepository.findByTrackingId(studentTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with trackingId: " + studentTrackingId));

        // Vérifie que l'utilisateur accède à son propre wallet uniquement
        SecurityUtils.verifyResourceOwnership(studentTrackingId);

        // Récupère le wallet HORIZON unique de l'étudiant
        com.backend.gns.domain.models.Wallet wallet = walletRepository.findByStudentTrackingId(studentTrackingId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for student: " + studentTrackingId));

        return walletMapper.toResponse(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeHistoriqueResponse> getCommandesOfStudent(UUID studentTrackingId) {
        // Vérifie que l'étudiant existe
        studentRepository.findByTrackingId(studentTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with trackingId: " + studentTrackingId));

        // Vérifie que l'utilisateur accède à ses propres commandes uniquement
        SecurityUtils.verifyResourceOwnership(studentTrackingId);

        // Récupère toutes les commandes de l'étudiant (déjà triée par dateCommande DESC dans la requête)
        List<com.backend.gns.domain.models.Commande> commandes = commandeRepository.findByStudentTrackingId(studentTrackingId);
        
        // Mappe les commandes en CommandeHistoriqueResponse
        return commandes.stream()
                .map(c -> new CommandeHistoriqueResponse(
                        c.getTrackingId(),
                        c.getReference(),
                        c.getMontantTotal(),
                        c.getDateCommande(),
                        c.getStatut().name(),
                        c.getMerchant() != null ? c.getMerchant().getNomBoutique() : null
                ))
                .toList();
    }
}
