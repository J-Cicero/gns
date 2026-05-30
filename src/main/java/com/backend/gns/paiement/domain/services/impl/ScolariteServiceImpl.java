package com.backend.gns.paiement.domain.services.impl;

import com.backend.gns.paiement.application.dtos.responses.PretScolariteResponse;
import com.backend.gns.paiement.application.mappers.PretScolariteMapper;
import com.backend.gns.academique.domain.models.Universite;
import com.backend.gns.academique.infrastructure.repositories.UniversiteRepository;
import com.backend.gns.wallet.domain.services.WalletService;
import com.backend.gns.paiement.domain.models.PretScolarite;
import com.backend.gns.paiement.domain.services.ScolariteService;
import com.backend.gns.paiement.infrastructure.repositories.PretScolariteRepository;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScolariteServiceImpl implements ScolariteService {

    private final PretScolariteRepository pretScolariteRepository;
    private final StudentRepository studentRepository;
    private final UniversiteRepository universiteRepository;
    private final InscriptionAnnuelleRepository inscriptionAnnuelleRepository;
    private final ScolariteYearRepository scolariteYearRepository;
    private final WalletService walletService;
    private final PretScolariteMapper pretScolariteMapper;

    @Override
    @Transactional
    public PretScolariteResponse demanderPretScolarite(UUID studentTrackingId, UUID universiteTrackingId, BigDecimal montant) {
        Student student = studentRepository.findByTrackingId(studentTrackingId)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));

        Universite universite = universiteRepository.findByTrackingId(universiteTrackingId)
                .orElseThrow(() -> new EntityNotFoundException("Université non trouvée"));

        // Trouver l'année scolaire en cours (ouverte)
        ScolariteYear currentYear = scolariteYearRepository.findByEstOuverteTrue()
                .orElseThrow(() -> new IllegalStateException("Aucune année scolaire ouverte pour le moment"));

        // Vérifier si l'étudiant est inscrit pour cette année
        InscriptionAnnuelle inscription = inscriptionAnnuelleRepository.findByStudentAndScolariteYear(student, currentYear)
                .orElseThrow(() -> new IllegalStateException("L'étudiant n'est pas inscrit pour l'année en cours"));

        PretScolarite pret = new PretScolarite();
        pret.setStudent(student);
        pret.setUniversite(universite);
        pret.setScolariteYear(currentYear);
        pret.setMontant(montant);
        pret.setEstRembourse(false);
        pret.setDescription("Paiement scolarité anticipé via StudCash");

        PretScolarite savedPret = pretScolariteRepository.save(pret);

        // Créditer l'Université immédiatement
        if (universite.getWallet() != null) {
            walletService.crediter(universite.getWallet().getTrackingId(), montant);
        } else {
            log.error("L'université {} n'a pas de wallet configuré", universite.getNom());
            throw new IllegalStateException("L'université n'a pas de wallet configuré");
        }

        log.info("Prêt scolarité créé pour l'étudiant {} d'un montant de {}", student.getNom(), montant);
        return pretScolariteMapper.toResponse(savedPret);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PretScolariteResponse> findByUniversite(UUID universiteTrackingId) {
        return pretScolariteRepository.findByUniversiteTrackingId(universiteTrackingId).stream()
                .map(pretScolariteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void rembourserPretsEnAttente(UUID studentTrackingId, BigDecimal montantDisponible) {
        List<PretScolarite> pretsNonRembourses = pretScolariteRepository.findByStudentTrackingIdAndEstRembourseFalse(studentTrackingId);
        
        BigDecimal resteApresRemboursement = montantDisponible;
        
        for (PretScolarite pret : pretsNonRembourses) {
            if (resteApresRemboursement.compareTo(pret.getMontant()) >= 0) {
                // On peut rembourser tout ce prêt
                resteApresRemboursement = resteApresRemboursement.subtract(pret.getMontant());
                pret.setEstRembourse(true);
                pretScolariteRepository.save(pret);
                
                // Débiter le wallet de l'étudiant (qui vient d'être crédité par la bourse)
                walletService.debiter(pret.getStudent().getWallet().getTrackingId(), pret.getMontant());
                log.info("Prêt scolarité {} remboursé automatiquement", pret.getTrackingId());
            } else if (resteApresRemboursement.compareTo(BigDecimal.ZERO) > 0) {
                // Remboursement partiel possible ? (À voir si on autorise)
                BigDecimal aDeduire = resteApresRemboursement;
                pret.setMontant(pret.getMontant().subtract(aDeduire));
                pretScolariteRepository.save(pret);
                
                walletService.debiter(pret.getStudent().getWallet().getTrackingId(), aDeduire);
                resteApresRemboursement = BigDecimal.ZERO;
                log.info("Remboursement partiel du prêt {}", pret.getTrackingId());
                break;
            }
        }
    }
}
