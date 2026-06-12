package com.backend.gns.student.domain.services.impl;

import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.services.InscriptionExterneService;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class InscriptionExterneServiceImpl implements InscriptionExterneService {

    private final InscriptionAnnuelleRepository inscriptionRepository;
    private final RestTemplate restTemplate;

    @Override
    public InscriptionAnnuelle synchroniserStatutInscription(InscriptionAnnuelle inscriptionAnnuelle) {
        String matricule = inscriptionAnnuelle.getStudent().getMatricule();
        
        if (matricule == null || matricule.isBlank()) {
            log.warn("Impossible de synchroniser : matricule absent pour l'étudiant {}", 
                inscriptionAnnuelle.getStudent().getTrackingId());
            return inscriptionAnnuelle;
        }

        log.info("Simulation appel API externe pour le matricule numérique: {}", matricule);

        // Simulation de la logique basée sur un matricule à 6 chiffres
        if (matricule.startsWith("1")) {
            // Cas : Matricule en 1xxxxx -> Bon travail
            inscriptionAnnuelle.setEstInscritDefinitif(true);
            inscriptionAnnuelle.setEstEligibleBourse(true);
            inscriptionAnnuelle.setTypeBourse(com.backend.gns.student.domain.enums.TypeBourse.BOURSE_EXCELLENCE);
            log.info("Mock API : Étudiant {} (6 chiffres) reconnu comme Excellence", matricule);
        } else if (matricule.startsWith("2")) {
            // Cas : Matricule en 2xxxxx -> Travail moyen
            inscriptionAnnuelle.setEstInscritDefinitif(true);
            inscriptionAnnuelle.setEstEligibleBourse(true);
            inscriptionAnnuelle.setTypeBourse(com.backend.gns.student.domain.enums.TypeBourse.BOURSE_MERITE);
            log.info("Mock API : Étudiant {} (6 chiffres) reconnu comme Mérite", matricule);
        } else {
            // Cas : Autres matricules (ex: 3xxxxx, 4xxxxx) -> Non boursier
            inscriptionAnnuelle.setEstInscritDefinitif(true);
            inscriptionAnnuelle.setEstEligibleBourse(false);
            inscriptionAnnuelle.setTypeBourse(null);
            log.info("Mock API : Étudiant {} (6 chiffres) reconnu comme Non Boursier", matricule);
        }
        
        inscriptionAnnuelle.setDateValidationApi(LocalDateTime.now());
        
        return inscriptionRepository.save(inscriptionAnnuelle);
    }
}
