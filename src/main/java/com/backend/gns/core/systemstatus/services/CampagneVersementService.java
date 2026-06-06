package com.backend.gns.core.systemstatus.services;

import com.backend.gns.core.systemstatus.dtos.ReconciliationReportDTO;
import com.backend.gns.core.systemstatus.enums.CampagneStatus;
import com.backend.gns.core.systemstatus.models.CampagneVersement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampagneVersementService {

  // Injecter le repository CampagneVersementRepository ici

  @Transactional
  public CampagneVersement createCampagne(String nomVague) {
    CampagneVersement campagne = new CampagneVersement();
    campagne.setTrackingId(UUID.randomUUID());
    campagne.setNomVague(nomVague);
    campagne.setStatut(CampagneStatus.PREPAREE);
    campagne.setDateInitiation(LocalDateTime.now());
    // Définir l'initiateur (Admin DBS) à partir du contexte de sécurité
    return null; // A remplacer par: repository.save(campagne);
  }

  @Transactional
  public void processCampagne(UUID trackingId) {
    // 1. Récupérer la campagne via trackingId
    // 2. Vérifier si statut == PREPAREE
    // 3. Appeler le service qui Gèle les wallets
    // 4. Appeler le service qui calcule les snapshots financiers
    // 5. Mettre à jour le statut en EN_COURS
  }

  public List<ReconciliationReportDTO> getReconciliationReport() {
    List<ReconciliationReportDTO> reports = new ArrayList<>();
    // Logique de réconciliation financière déplacée ici
    return reports;
  }
}
