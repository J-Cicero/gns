package com.backend.gns.wallet.domain.services.impl;

import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.paiement.domain.services.ScolariteService;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.EligibiliteService;
import com.backend.gns.student.domain.services.EligibiliteService.EligibiliteResult;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import com.backend.gns.wallet.domain.services.MassVersementService;
import com.backend.gns.wallet.domain.services.WalletService;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MassVersementServiceImpl implements MassVersementService {

  private final InscriptionAnnuelleRepository inscriptionAnnuelleRepository;
  private final ScolariteYearRepository scolariteYearRepository;
  private final EligibiliteService eligibiliteService;
  private final WalletService walletService;
  private final WalletRepository walletRepository;
  private final ScolariteService scolariteService;
  private final BoutiqueRepository boutiqueRepository;

  @Override
  @Transactional
  public void versementMasseEtudiants(UUID scolariteYearTrackingId, BigDecimal montantFixe) {
    ScolariteYear year =
        scolariteYearRepository
            .findByTrackingId(scolariteYearTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Année scolaire non trouvée"));

    // On récupère toutes les inscriptions de cette année
    List<InscriptionAnnuelle> inscriptions =
        inscriptionAnnuelleRepository.findAllByScolariteYear(year);

    for (InscriptionAnnuelle ins : inscriptions) {
      Student student = ins.getStudent();

      // On vérifie l'éligibilité complète
      EligibiliteResult result =
          eligibiliteService.verifierEligibilite(student, ins, student.getBanqueEtudiant());

      if (result.estEligible) {
        BigDecimal montantAVerser = (montantFixe != null) ? montantFixe : result.plafondAccorde;
        if (student.getWallet() != null) {
          try {
            // 1. Créditer le montant de la bourse
            walletService.crediter(student.getWallet().getTrackingId(), montantAVerser);

            // 2. Rembourser immédiatement les dettes de scolarité
            scolariteService.rembourserPretsEnAttente(student.getTrackingId(), montantAVerser);

            log.info("Versement effectué pour l'étudiant {}: {}", student.getNom(), montantAVerser);
          } catch (Exception e) {
            log.error(
                "Erreur lors du versement pour l'étudiant {}: {}",
                student.getNom(),
                e.getMessage());
          }
        }
      } else {
        log.warn("Étudiant {} non éligible: {}", student.getNom(), result.motifRejet);
      }
    }
  }

  @Override
  @Transactional
  public void versementMasseBoutiques(BigDecimal seuil, BigDecimal montantAVerser) {
    List<Boutique> boutiques = boutiqueRepository.findAll();

    for (Boutique boutique : boutiques) {
      if (boutique.getWallet() != null && boutique.getWallet().getSolde().compareTo(seuil) <= 0) {
        try {
          walletService.crediter(boutique.getWallet().getTrackingId(), montantAVerser);
          log.info("Quota accordé à la boutique {}: {}", boutique.getNomBoutique(), montantAVerser);
        } catch (Exception e) {
          log.error("Erreur versement boutique {}: {}", boutique.getNomBoutique(), e.getMessage());
        }
      }
    }
  }
}
