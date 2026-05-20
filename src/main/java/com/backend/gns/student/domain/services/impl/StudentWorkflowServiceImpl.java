package com.backend.gns.student.domain.services.impl;

import com.backend.gns.Shared.exception.ResourceNotFoundException;
import com.backend.gns.Shared.wallet.domain.enums.WalletStatus;
import com.backend.gns.Shared.wallet.domain.enums.WalletType;
import com.backend.gns.Shared.wallet.domain.models.Wallet;
import com.backend.gns.student.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.student.application.mappers.InscriptionAnnuelleMapper;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.services.EligibiliteService;
import com.backend.gns.student.domain.services.StudentWorkflowService;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentWorkflowServiceImpl implements StudentWorkflowService {

    private final InscriptionAnnuelleRepository inscriptionRepository;
    private final InscriptionAnnuelleMapper inscriptionMapper;
    private final EligibiliteService eligibiliteService;

    @Override
    @Transactional
    public InscriptionAnnuelleResponse validerEtActiverInscription(UUID inscriptionTrackingId) {
        log.info("Démarrage du workflow de validation pour l'inscription: {}", inscriptionTrackingId);

        // 1. Récupérer l'inscription
        InscriptionAnnuelle inscription = inscriptionRepository.findByTrackingId(inscriptionTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription non trouvée: " + inscriptionTrackingId));

        // 2. Vérifier l'éligibilité (Appel du service métier)
        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(
                inscription.getStudent(),
                inscription,
                inscription.getStudent().getBanqueEtudiant()
        );

        if (result.estEligible) {
            log.info("Étudiant éligible. Plafond accordé: {}", result.plafondAccorde);
            
            // 3. Mise à jour de l'inscription
            inscription.setPlafondAccorde(result.plafondAccorde);
            
            // 4. Mise à jour du Wallet de l'étudiant
            Wallet wallet = inscription.getStudent().getWallet();
            if (wallet != null) {
                wallet.setPlafond(result.plafondAccorde);
                wallet.setStatutWallet(WalletStatus.ACTIF);
                
                // Logique de type de Wallet (basée sur le montant par exemple)
                if (result.plafondAccorde.compareTo(new BigDecimal("36000")) <= 0) {
                    wallet.setTypeWallet(WalletType.STUDENT_36k);
                } else {
                    wallet.setTypeWallet(WalletType.STUDENT_54k);
                }
                
                log.info("Wallet activé pour l'étudiant {}: Type={}, Plafond={}", 
                        inscription.getStudent().getTrackingId(), wallet.getTypeWallet(), wallet.getPlafond());
            }
        } else {
            log.warn("Étudiant non éligible pour l'inscription {}. Motif: {}", 
                    inscriptionTrackingId, result.motifRejet);
            inscription.setPlafondAccorde(BigDecimal.ZERO);
            // On peut décider de laisser le wallet INACTIF ou de le passer en standard sans bourse
        }

        InscriptionAnnuelle savedInscription = inscriptionRepository.save(inscription);
        return inscriptionMapper.toResponse(savedInscription);
    }
}
