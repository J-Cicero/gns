package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.application.mappers.PaiementMapper;
import com.backend.gns.application.mappers.WalletMapper;
import com.backend.gns.domain.dtos.requests.WalletRequest;
import com.backend.gns.domain.dtos.requests.VersementRequest;
import com.backend.gns.domain.dtos.responses.WalletResponse;
import com.backend.gns.domain.dtos.responses.PaiementResponse;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.domain.enums.WalletType;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import com.backend.gns.infrastructure.repositories.PaiementRepository;
import com.backend.gns.domain.services.WalletService;
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
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final VersementService versementService;
    private final PaiementRepository paiementRepository;
    private final PaiementMapper paiementMapper;

    @Override
    public WalletResponse create(WalletRequest request) {
        Wallet wallet = walletMapper.toEntity(request);
        Wallet savedWallet = walletRepository.save(wallet);
        return walletMapper.toResponse(savedWallet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletResponse> getAll() {
        List<Wallet> wallets = walletRepository.findAll();
        return walletMapper.toResponseList(wallets);
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getByTrackingId(UUID trackingId) {
        Wallet wallet = walletRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with trackingId: " + trackingId));
        return walletMapper.toResponse(wallet);
    }

    @Override
    public WalletResponse update(UUID trackingId, WalletRequest request) {
        Wallet wallet = walletRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with trackingId: " + trackingId));

        wallet.setTypeWallet(com.backend.gns.domain.enums.WalletType.valueOf(request.typeWallet()));
        wallet.setSolde(request.solde());
        wallet.setPlafond(request.plafond());

        Wallet updatedWallet = walletRepository.save(wallet);
        return walletMapper.toResponse(updatedWallet);
    }

    @Override
    public void delete(UUID trackingId) {
        Wallet wallet = walletRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with trackingId: " + trackingId));
        walletRepository.delete(wallet);
    }

    @Override
    public WalletResponse verrouillerWallet(UUID walletTrackingId) {
        Wallet wallet = walletRepository.findByTrackingId(walletTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletTrackingId));
        wallet.setEstVerrouille(true);
        Wallet updatedWallet = walletRepository.save(wallet);
        return walletMapper.toResponse(updatedWallet);
    }

    @Override
    public WalletResponse deverrouillerWallet(UUID walletTrackingId) {
        // F6 - Recupere le wallet par trackingId
        Wallet wallet = walletRepository.findByTrackingId(walletTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletTrackingId));

        // Verifie que estVerrouille = true
        if (!wallet.getEstVerrouille()) {
            throw new IllegalStateException("Wallet is not locked");
        }

        // Passe estVerrouille a false
        wallet.setEstVerrouille(false);

        Wallet updatedWallet = walletRepository.save(wallet);
        return walletMapper.toResponse(updatedWallet);
    }

    @Override
    public WalletResponse crediterHorizon(UUID walletTrackingId) {
        // F2 - Recupere le wallet HORIZON par trackingId
        Wallet wallet = walletRepository.findByTrackingId(walletTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletTrackingId));

        // Verifie que c'est un wallet HORIZON
        if (wallet.getTypeWallet() != WalletType.HORIZON) {
            throw new IllegalStateException("Wallet must be of type HORIZON");
        }

        // Utilise le plafond fixe de 36000 FCFA
        Double plafondFixe = 36000.0;

        // Calcule les 14/15 du plafond : 36000 * (14/15) = 33600 FCFA
        Double credit = plafondFixe * (14.0 / 15.0);

        // Credite le montant sur le solde
        wallet.setSolde(wallet.getSolde() + credit);

        // Sauvegarde le wallet credite
        Wallet updatedWallet = walletRepository.save(wallet);

        // Cree un versement BOURSE_DBS avec statut EXECUTE et dateEffective = aujourd'hui
        VersementRequest versementRequest = new VersementRequest(
                walletTrackingId,
                credit,
                "BOURSE_DBS",
                LocalDate.now()
        );
        versementService.creerVersementExecute(versementRequest);

        return walletMapper.toResponse(updatedWallet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> getPaiementsOfWallet(UUID walletTrackingId) {
        // Vérifie que le wallet existe
        walletRepository.findByTrackingId(walletTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with trackingId: " + walletTrackingId));

        // Récupère tous les paiements du wallet (déjà triés par dateTimestamp DESC dans la requête)
        List<com.backend.gns.domain.models.Paiement> paiements = paiementRepository.findByWalletTrackingId(walletTrackingId);
        return paiementMapper.toResponseList(paiements);
    }
}
