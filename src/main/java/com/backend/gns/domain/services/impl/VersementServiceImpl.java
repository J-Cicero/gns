package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.domain.dtos.requests.VersementRequest;
import com.backend.gns.domain.dtos.responses.VersementResponse;
import com.backend.gns.domain.enums.VersementStatut;
import com.backend.gns.domain.enums.VersementType;
import com.backend.gns.domain.mappers.VersementMapper;
import com.backend.gns.domain.models.Versement;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.infrastructure.repositories.VersementRepository;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import com.backend.gns.domain.services.VersementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class VersementServiceImpl implements VersementService {

    private final VersementRepository versementRepository;
    private final VersementMapper versementMapper;
    private final WalletRepository walletRepository;

    @Override
    public VersementResponse create(VersementRequest request) {
        Versement versement = versementMapper.toEntity(request);
        Versement savedVersement = versementRepository.save(versement);
        return versementMapper.toResponse(savedVersement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VersementResponse> getAll() {
        List<Versement> versements = versementRepository.findAll();
        return versementMapper.toResponseList(versements);
    }

    @Override
    @Transactional(readOnly = true)
    public VersementResponse getByTrackingId(UUID trackingId) {
        Versement versement = versementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Versement not found with trackingId: " + trackingId));
        return versementMapper.toResponse(versement);
    }

    @Override
    public VersementResponse update(UUID trackingId, VersementRequest request) {
        Versement versement = versementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Versement not found with trackingId: " + trackingId));

        versement.setMontantVerse(request.montantVerse());
        versement.setTypeVersement(com.backend.gns.domain.enums.VersementType.valueOf(request.typeVersement()));
        versement.setDatePrevue(request.datePrevue());

        Versement updatedVersement = versementRepository.save(versement);
        return versementMapper.toResponse(updatedVersement);
    }

    @Override
    public void delete(UUID trackingId) {
        Versement versement = versementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Versement not found with trackingId: " + trackingId));
        versementRepository.delete(versement);
    }

    @Override
    public VersementResponse creerVersementExecute(VersementRequest request) {
        // Recupere le wallet par trackingId
        Wallet wallet = walletRepository.findByTrackingId(request.walletTrackingId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + request.walletTrackingId()));

        // Cree le versement directement avec statut EXECUTE
        Versement versement = new Versement();
        versement.setTrackingId(UUID.randomUUID());
        versement.setWallet(wallet);
        versement.setMontantVerse(request.montantVerse());
        versement.setTypeVersement(VersementType.valueOf(request.typeVersement()));
        versement.setDatePrevue(request.datePrevue());
        versement.setDateEffective(LocalDate.now());
        versement.setStatut(VersementStatut.EXECUTE);

        Versement savedVersement = versementRepository.save(versement);
        return versementMapper.toResponse(savedVersement);
    }

    @Override
    public VersementResponse executerRemboursementDBS(UUID versementTrackingId) {
        // F8 - Recupere le versement par trackingId
        Versement versement = versementRepository.findByTrackingId(versementTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Versement not found: " + versementTrackingId));

        // Verifie que type = BOURSE_DBS et statut = PLANIFIE
        if (versement.getTypeVersement() != VersementType.BOURSE_DBS) {
            throw new IllegalStateException("Versement must be of type BOURSE_DBS");
        }

        if (versement.getStatut() != VersementStatut.PROGRAMME) {
            throw new IllegalStateException("Versement must have status PROGRAMME");
        }

        // Recupere le wallet Horizon lié au versement
        Wallet wallet = versement.getWallet();

        // Recalcule et recrédite les 14/15 du plafond pour le trimestre suivant
        Double creditTrimestrique = wallet.getPlafond() * (14.0 / 15.0);
        wallet.setSolde(creditTrimestrique);
        walletRepository.save(wallet);

        // Cree un nouveau versement PLANIFIE pour le prochain trimestre
        Versement nouveauVersement = new Versement();
        nouveauVersement.setTrackingId(UUID.randomUUID());
        nouveauVersement.setWallet(wallet);
        nouveauVersement.setMontantVerse(creditTrimestrique);
        nouveauVersement.setTypeVersement(VersementType.BOURSE_DBS);
        nouveauVersement.setDatePrevue(LocalDate.now().plusMonths(3));
        nouveauVersement.setStatut(VersementStatut.PROGRAMME);

        versementRepository.save(nouveauVersement);

        // Passe le versement actuel à statut EXECUTE avec dateEffective = aujourd'hui
        versement.setStatut(VersementStatut.EXECUTE);
        versement.setDateEffective(LocalDate.now());

        Versement savedVersement = versementRepository.save(versement);
        return versementMapper.toResponse(savedVersement);
    }
}
