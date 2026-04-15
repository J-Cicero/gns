package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.Shared.security.utils.SecurityUtils;
import com.backend.gns.application.mappers.BudgetVirtuelMapper;
import com.backend.gns.application.mappers.MerchantMapper;
import com.backend.gns.domain.dtos.requests.MerchantRequest;
import com.backend.gns.domain.dtos.responses.MerchantResponse;
import com.backend.gns.domain.dtos.responses.BudgetVirtuelResponse;
import com.backend.gns.domain.dtos.responses.CommandeHistoriqueResponse;
import com.backend.gns.domain.models.Merchant;
import com.backend.gns.infrastructure.repositories.MerchantRepository;
import com.backend.gns.infrastructure.repositories.BudgetVirtuelRepository;
import com.backend.gns.infrastructure.repositories.CommandeRepository;
import com.backend.gns.domain.services.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;
    private final BudgetVirtuelRepository budgetVirtuelRepository;
    private final BudgetVirtuelMapper budgetVirtuelMapper;
    private final CommandeRepository commandeRepository;

    @Override
    public MerchantResponse create(MerchantRequest request) {
        Merchant merchant = merchantMapper.toEntity(request);
        Merchant savedMerchant = merchantRepository.save(merchant);
        return merchantMapper.toResponse(savedMerchant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchantResponse> getAll() {
        List<Merchant> merchants = merchantRepository.findAll();
        return merchantMapper.toResponseList(merchants);
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantResponse getByTrackingId(UUID trackingId) {
        Merchant merchant = merchantRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found with trackingId: " + trackingId));
        
        // Vérifier que l'utilisateur accède à ses propres données uniquement
        SecurityUtils.verifyResourceOwnership(trackingId);
        
        return merchantMapper.toResponse(merchant);
    }

    @Override
    public MerchantResponse update(UUID trackingId, MerchantRequest request) {
        Merchant merchant = merchantRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found with trackingId: " + trackingId));

        // Vérifier que l'utilisateur modifie ses propres données uniquement
        SecurityUtils.verifyResourceOwnership(trackingId);

        merchant.setNom(request.nom());
        merchant.setPrenom(request.prenom());
        merchant.setEmail(request.email());
        merchant.setMotDePasse(request.motDePasse());
        merchant.setTelephone(request.telephone());
        merchant.setNomBoutique(request.nomBoutique());
        merchant.setCheminCarteEDJ(request.cheminCarteEDJ());
        merchant.setCategorieShop(request.categorieShop());

        Merchant updatedMerchant = merchantRepository.save(merchant);
        return merchantMapper.toResponse(updatedMerchant);
    }

    @Override
    public void delete(UUID trackingId) {
        Merchant merchant = merchantRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found with trackingId: " + trackingId));
        merchantRepository.delete(merchant);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetVirtuelResponse getBudgetActif(UUID merchantTrackingId) {
        merchantRepository.findByTrackingId(merchantTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found: " + merchantTrackingId));
        SecurityUtils.verifyResourceOwnership(merchantTrackingId);
        String periodeMoisCourant = YearMonth.now().toString();
        com.backend.gns.domain.models.BudgetVirtuel budget = budgetVirtuelRepository
                .findByMerchantTrackingIdAndPeriode(merchantTrackingId, periodeMoisCourant)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found for period: " + periodeMoisCourant));
        return budgetVirtuelMapper.toResponse(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeHistoriqueResponse> getCommandesRecues(UUID merchantTrackingId) {
        merchantRepository.findByTrackingId(merchantTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found: " + merchantTrackingId));
        SecurityUtils.verifyResourceOwnership(merchantTrackingId);
        List<com.backend.gns.domain.models.Commande> commandes = commandeRepository.findByMerchantTrackingId(merchantTrackingId);
        return commandes.stream()
                .map(c -> new CommandeHistoriqueResponse(
                        c.getTrackingId(),
                        c.getReference(),
                        c.getMontantTotal(),
                        c.getDateCommande(),
                        c.getStatut().name(),
                        c.getStudent() != null ? (c.getStudent().getNom() + " " + c.getStudent().getPrenom()) : null
                ))
                .toList();
    }
}
