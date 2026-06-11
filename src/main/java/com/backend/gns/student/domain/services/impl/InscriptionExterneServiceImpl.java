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
        log.info("Appel API externe pour l'étudiant: {}", inscriptionAnnuelle.getStudent().getNumEtudiantUniv());

        // TODO: Implémenter l'appel REST réel vers l'API de l'Université
        // Exemple: ApiResponse response = restTemplate.getForObject(url, ApiResponse.class, ...);
        
        // Simulation de la réponse pour le moment:
        // inscriptionAnnuelle.setEstInscritDefinitif(response.isEnrolled());
        // inscriptionAnnuelle.setEstEligibleBourse(response.isEligible());
        // inscriptionAnnuelle.setTypeBourse(TypeBourse.valueOf(response.getType()));
        
        inscriptionAnnuelle.setDateValidationApi(LocalDateTime.now());
        
        return inscriptionRepository.save(inscriptionAnnuelle);
    }
}
