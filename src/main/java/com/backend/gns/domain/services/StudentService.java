package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.StudentRequest;
import com.backend.gns.domain.dtos.responses.StudentResponse;
import com.backend.gns.domain.dtos.responses.WalletResponse;
import com.backend.gns.domain.dtos.responses.CommandeHistoriqueResponse;

import java.util.List;
import java.util.UUID;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    List<StudentResponse> getAll();

    StudentResponse getByTrackingId(UUID trackingId);

    StudentResponse update(UUID trackingId, StudentRequest request);

    void delete(UUID trackingId);

    /**
     * F1 - Valide le KYC de l'etudiant et cree ses wallets.
     * Cree toujours RELAIS, et HORIZON si eligible et boursier.
     */
    StudentResponse validerKYC(UUID studentTrackingId);

    /**
     * C1 - Récupère le wallet HORIZON de l'étudiant
     */
    WalletResponse getWalletOfStudent(UUID studentTrackingId);

    /**
     * C2 - Récupère l'historique des commandes d'un étudiant
     */
    List<CommandeHistoriqueResponse> getCommandesOfStudent(UUID studentTrackingId);
}
