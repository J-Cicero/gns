package com.backend.gns.application.controllers;

import com.backend.gns.application.mappers.StudentMapper;
import com.backend.gns.application.mappers.VersementMapper;
import com.backend.gns.domain.dtos.responses.AdminStatistiquesResponse;
import com.backend.gns.domain.models.Student;
import com.backend.gns.domain.models.Versement;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.domain.enums.PaiementStatut;
import com.backend.gns.domain.enums.VersementStatut;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import com.backend.gns.infrastructure.repositories.MerchantRepository;
import com.backend.gns.infrastructure.repositories.PaiementRepository;
import com.backend.gns.infrastructure.repositories.VersementRepository;
import com.backend.gns.domain.dtos.responses.StudentResponse;
import com.backend.gns.domain.dtos.responses.VersementResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contrôleur AdminController pour les statistiques et gestion administrative
 * Accès réservé aux administrateurs (ADMIN)
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "AdminController", description = "Statistiques et gestion administrative")
@SecurityRequirement(name = "bearerAuth")
public class AdminStatsController {

    private final StudentRepository studentRepository;
    private final MerchantRepository merchantRepository;
    private final PaiementRepository paiementRepository;
    private final VersementRepository versementRepository;
    private final StudentMapper studentMapper;
    private final VersementMapper versementMapper;

    @GetMapping("/statistiques")
    @Operation(summary = "Récupérer les statistiques globales", description = "Retourne le tableau de bord des statistiques adminitratives : nombre total d'étudiants et commerçants validés, total des transactions, volume transactionnel, commissions collectées, versements en attente et dossiers KYC en attente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<AdminStatistiquesResponse> getStatistiques() {
        // Compte les étudiants et commerçants avec KYC VALIDE
        Long totalEtudiants = studentRepository.countByStatutKYC(KycStatus.VALIDE);
        Long totalCommerçants = merchantRepository.countByStatutKYC(KycStatus.VALIDE);

        // Compte et somme les paiements VALIDE
        Long totalTransactions = paiementRepository.countByStatut(PaiementStatut.VALIDE);
        Double volumeTotal = paiementRepository.sumMontantDebiteByStatut(PaiementStatut.VALIDE);
        Double commissionsCollectees = paiementRepository.sumCommissionByStatut(PaiementStatut.VALIDE);

        // Versements en attente (statut PROGRAMME)
        Long versementsEnAttente = versementRepository.countByStatut(VersementStatut.PROGRAMME);

        // Dossiers KYC en attente
        Long dossiersKycEnAttente = studentRepository.countByStatutKYC(KycStatus.EN_ATTENTE);

        AdminStatistiquesResponse stats = new AdminStatistiquesResponse(
                totalEtudiants != null ? totalEtudiants : 0L,
                totalCommerçants != null ? totalCommerçants : 0L,
                totalTransactions != null ? totalTransactions : 0L,
                volumeTotal != null ? volumeTotal : 0.0,
                commissionsCollectees != null ? commissionsCollectees : 0.0,
                versementsEnAttente != null ? versementsEnAttente : 0L,
                dossiersKycEnAttente != null ? dossiersKycEnAttente : 0L
        );

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/kyc-en-attente")
    @Operation(summary = "Lister les dossiers KYC en attente", description = "Retourne la liste des étudiants avec statut KYC EN_ATTENTE, triée par date d'inscription croissante (plus anciens en premier)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dossiers récupérés"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<List<StudentResponse>> getKycEnAttente() {
        List<Student> studentsEnAttente = studentRepository.findByStatutKYCOrderByCreatedAt(KycStatus.EN_ATTENTE);
        List<StudentResponse> responses = studentMapper.toResponseList(studentsEnAttente);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/versements-planifies")
    @Operation(summary = "Lister les versements planifiés", description = "Retourne la liste de tous les versements avec statut PROGRAMME, triée par date prévue croissante (plus urgent en premier)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Versements récupérés"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<List<VersementResponse>> getVersementsPlanifies() {
        List<Versement> versementsPlanifies = versementRepository.findByStatut(VersementStatut.PROGRAMME);
        List<VersementResponse> responses = versementMapper.toResponseList(versementsPlanifies);
        return ResponseEntity.ok(responses);
    }
}
