package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.domain.dtos.requests.PaiementRequest;
import com.backend.gns.domain.dtos.requests.PaiementScolariteRequest;
import com.backend.gns.domain.dtos.requests.PaiementSimpleRequest;
import com.backend.gns.domain.dtos.requests.PaiementHybrideRequest;
import com.backend.gns.domain.dtos.responses.PaiementResponse;
import com.backend.gns.domain.enums.PaiementStatut;
import com.backend.gns.domain.enums.PaiementType;
import com.backend.gns.domain.enums.WalletType;
import com.backend.gns.domain.mappers.PaiementMapper;
import com.backend.gns.domain.models.Paiement;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.domain.models.Commande;
import com.backend.gns.infrastructure.repositories.PaiementRepository;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import com.backend.gns.infrastructure.repositories.CommandeRepository;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import com.backend.gns.domain.services.PaiementService;
import com.backend.gns.domain.services.BudgetVirtuelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final PaiementMapper paiementMapper;
    private final WalletRepository walletRepository;
    private final CommandeRepository commandeRepository;
    private final StudentRepository studentRepository;
    private final BudgetVirtuelService budgetVirtuelService;

    @Override
    public PaiementResponse create(PaiementRequest request) {
        Paiement paiement = paiementMapper.toEntity(request);
        Paiement savedPaiement = paiementRepository.save(paiement);
        return paiementMapper.toResponse(savedPaiement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> getAll() {
        List<Paiement> paiements = paiementRepository.findAll();
        return paiementMapper.toResponseList(paiements);
    }

    @Override
    @Transactional(readOnly = true)
    public PaiementResponse getByTrackingId(UUID trackingId) {
        Paiement paiement = paiementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement not found with trackingId: " + trackingId));
        return paiementMapper.toResponse(paiement);
    }

    @Override
    public PaiementResponse update(UUID trackingId, PaiementRequest request) {
        Paiement paiement = paiementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement not found with trackingId: " + trackingId));

        paiement.setMontantProduit(request.montantProduit());
        paiement.setCommission(request.commission());
        paiement.setMontantDebite(request.montantDebite());
        paiement.setTypePaiement(com.backend.gns.domain.enums.PaiementType.valueOf(request.typePaiement()));

        Paiement updatedPaiement = paiementRepository.save(paiement);
        return paiementMapper.toResponse(updatedPaiement);
    }

    @Override
    public void delete(UUID trackingId) {
        Paiement paiement = paiementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement not found with trackingId: " + trackingId));
        paiementRepository.delete(paiement);
    }

    @Override
    public PaiementResponse effectuerPaiementScolarite(PaiementScolariteRequest request) {
        // F7 - Recupere le wallet HORIZON par trackingId
        Wallet wallet = walletRepository.findByTrackingId(request.walletTrackingId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + request.walletTrackingId()));

        // Verifie que c'est un wallet HORIZON
        if (wallet.getTypeWallet() != WalletType.HORIZON) {
            throw new IllegalStateException("Wallet must be of type HORIZON");
        }

        // Verifie que estVerrouille = true
        if (!wallet.getEstVerrouille()) {
            throw new IllegalStateException("Wallet must be locked for tuition payment");
        }

        // Verifie que le solde est suffisant
        if (wallet.getSolde() < request.montantScolarite()) {
            throw new IllegalStateException("Insufficient balance");
        }

        // Recupere la commande
        Commande commande = commandeRepository.findByTrackingId(request.commandeTrackingId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found: " + request.commandeTrackingId()));

        // Débite le wallet (pas de commission sur la scolarité)
        wallet.setSolde(wallet.getSolde() - request.montantScolarite());
        walletRepository.save(wallet);

        // Crée et enregistre le paiement de type SCOLARITE
        Paiement paiement = new Paiement();
        paiement.setTrackingId(UUID.randomUUID());
        paiement.setCommande(commande);
        paiement.setWallet(wallet);
        paiement.setMontantProduit(request.montantScolarite());
        paiement.setCommission(0.0);
        paiement.setMontantDebite(request.montantScolarite());
        paiement.setDateTimestamp(LocalDateTime.now());
        paiement.setTypePaiement(PaiementType.SCOLARITE);
        paiement.setStatutPaiement(PaiementStatut.VALIDE);
        paiement.setEstSwitch(false);
        paiement.setCommandeRef(commande.getReference());

        Paiement savedPaiement = paiementRepository.save(paiement);
        return paiementMapper.toResponse(savedPaiement);
    }

    @Override
    public PaiementResponse effectuerPaiement(PaiementSimpleRequest request) {
        // F4 - Recupere le wallet et la commande
        Wallet wallet = walletRepository.findByTrackingId(request.walletTrackingId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + request.walletTrackingId()));
        
        Commande commande = commandeRepository.findByTrackingId(request.commandeTrackingId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found: " + request.commandeTrackingId()));

        // Verifie que le wallet n'est pas verrouille
        if (wallet.getEstVerrouille()) {
            throw new IllegalStateException("Wallet is locked");
        }

        // Calcule la commission : montantProduit * 0.02
        Double commission = request.montantProduit() * 0.02;
        Double montantTotal = request.montantProduit() + commission;

        // Verifie que le solde du wallet >= montantTotal
        if (wallet.getSolde() < montantTotal) {
            throw new IllegalStateException("Insufficient balance");
        }

        // Recupere et verifie le budget virtuel du commercant (appelle F10)
        budgetVirtuelService.verifierEtDebiterBudget(commande.getMerchant().getTrackingId(), request.montantProduit());

        // Debite le wallet
        wallet.setSolde(wallet.getSolde() - montantTotal);
        walletRepository.save(wallet);

        // Cree et enregistre le paiement
        Paiement paiement = new Paiement();
        paiement.setTrackingId(UUID.randomUUID());
        paiement.setCommande(commande);
        paiement.setWallet(wallet);
        paiement.setMontantProduit(request.montantProduit());
        paiement.setCommission(commission);
        paiement.setMontantDebite(montantTotal);
        paiement.setDateTimestamp(LocalDateTime.now());
        paiement.setTypePaiement(PaiementType.ACHAT);
        paiement.setStatutPaiement(PaiementStatut.VALIDE);
        paiement.setEstSwitch(false);
        paiement.setCommandeRef(commande.getReference());

        Paiement savedPaiement = paiementRepository.save(paiement);
        return paiementMapper.toResponse(savedPaiement);
    }

    @Override
    public PaiementResponse effectuerPaiementHybride(PaiementHybrideRequest request) {
        // F5 - Recupere le wallet principal et la commande
        Wallet walletPrincipal = walletRepository.findByTrackingId(request.walletPrincipalTrackingId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + request.walletPrincipalTrackingId()));
        
        Commande commande = commandeRepository.findByTrackingId(request.commandeTrackingId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found: " + request.commandeTrackingId()));

        // Calcule commission et montantTotal
        Double commission = request.montantProduit() * 0.02;
        Double montantTotal = request.montantProduit() + commission;

        // Si solde wallet principal >= montantTotal -> execute paiement simple
        if (walletPrincipal.getSolde() >= montantTotal) {
            return effectuerPaiement(new PaiementSimpleRequest(
                    request.walletPrincipalTrackingId(),
                    request.commandeTrackingId(),
                    request.montantProduit()
            ));
        }

        // Sinon : paiement hybride a deux wallets
        // Recupere et verifie le budget virtuel (appelle F10)
        budgetVirtuelService.verifierEtDebiterBudget(commande.getMerchant().getTrackingId(), request.montantProduit());

        // Paiement 1 : tout le solde du wallet principal
        Double soldeWalletPrincipal = walletPrincipal.getSolde();
        Double resteAPayer = montantTotal - soldeWalletPrincipal;

        // Debite le wallet principal
        walletPrincipal.setSolde(0.0);
        walletRepository.save(walletPrincipal);

        // Cree paiement 1 avec estSwitch = false
        Paiement paiement1 = new Paiement();
        paiement1.setTrackingId(UUID.randomUUID());
        paiement1.setCommande(commande);
        paiement1.setWallet(walletPrincipal);
        paiement1.setMontantProduit(soldeWalletPrincipal);
        paiement1.setCommission(0.0);
        paiement1.setMontantDebite(soldeWalletPrincipal);
        paiement1.setDateTimestamp(LocalDateTime.now());
        paiement1.setTypePaiement(PaiementType.ACHAT);
        paiement1.setStatutPaiement(PaiementStatut.VALIDE);
        paiement1.setEstSwitch(false);
        paiement1.setCommandeRef(commande.getReference());
        paiementRepository.save(paiement1);

        // Recupere le second wallet de l'etudiant
        Wallet walletSecond = walletRepository.findByStudentTrackingIdAndType(
                commande.getStudent().getTrackingId(),
                walletPrincipal.getTypeWallet() == WalletType.RELAIS ? WalletType.HORIZON : WalletType.RELAIS
        ).orElseThrow(() -> new ResourceNotFoundException("Second wallet not found for hybrid payment"));

        // Verifie que le second wallet a assez
        if (walletSecond.getSolde() < resteAPayer) {
            throw new IllegalStateException("Insufficient balance in both wallets");
        }

        // Debite le second wallet
        walletSecond.setSolde(walletSecond.getSolde() - resteAPayer);
        walletRepository.save(walletSecond);

        // Cree paiement 2 avec estSwitch = true, meme commandeRef
        Paiement paiement2 = new Paiement();
        paiement2.setTrackingId(UUID.randomUUID());
        paiement2.setCommande(commande);
        paiement2.setWallet(walletSecond);
        paiement2.setMontantProduit(resteAPayer - (resteAPayer * 0.02));
        paiement2.setCommission(resteAPayer * 0.02);
        paiement2.setMontantDebite(resteAPayer);
        paiement2.setDateTimestamp(LocalDateTime.now());
        paiement2.setTypePaiement(PaiementType.ACHAT);
        paiement2.setStatutPaiement(PaiementStatut.VALIDE);
        paiement2.setEstSwitch(true);
        paiement2.setCommandeRef(commande.getReference());

        Paiement savedPaiement = paiementRepository.save(paiement2);
        return paiementMapper.toResponse(savedPaiement);
    }
}
