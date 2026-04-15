package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.application.mappers.BudgetVirtuelMapper;
import com.backend.gns.Shared.security.exceptions.AlreadyExistException;
import com.backend.gns.domain.dtos.requests.BudgetVirtuelRequest;
import com.backend.gns.domain.dtos.responses.BudgetVirtuelResponse;
import com.backend.gns.domain.models.BudgetVirtuel;
import com.backend.gns.domain.models.Merchant;
import com.backend.gns.infrastructure.repositories.BudgetVirtuelRepository;
import com.backend.gns.infrastructure.repositories.MerchantRepository;
import com.backend.gns.domain.services.BudgetVirtuelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BudgetVirtuelServiceImpl implements BudgetVirtuelService {

    private final BudgetVirtuelRepository budgetVirtuelRepository;
    private final BudgetVirtuelMapper budgetVirtuelMapper;
    private final MerchantRepository merchantRepository;

    @Override
    public BudgetVirtuelResponse create(BudgetVirtuelRequest request) {
        BudgetVirtuel budget = budgetVirtuelMapper.toEntity(request);
        BudgetVirtuel savedBudget = budgetVirtuelRepository.save(budget);
        return budgetVirtuelMapper.toResponse(savedBudget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetVirtuelResponse> getAll() {
        List<BudgetVirtuel> budgets = budgetVirtuelRepository.findAll();
        return budgetVirtuelMapper.toResponseList(budgets);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetVirtuelResponse getByTrackingId(UUID trackingId) {
        BudgetVirtuel budget = budgetVirtuelRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("BudgetVirtuel not found with trackingId: " + trackingId));
        return budgetVirtuelMapper.toResponse(budget);
    }

    @Override
    public BudgetVirtuelResponse update(UUID trackingId, BudgetVirtuelRequest request) {
        BudgetVirtuel budget = budgetVirtuelRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("BudgetVirtuel not found with trackingId: " + trackingId));

        budget.setMontantAlloue(request.montantAlloue());
        budget.setMontantRestant(request.montantAlloue());
        budget.setPeriodeMois(request.periodeMois());

        BudgetVirtuel updatedBudget = budgetVirtuelRepository.save(budget);
        return budgetVirtuelMapper.toResponse(updatedBudget);
    }

    @Override
    public void delete(UUID trackingId) {
        BudgetVirtuel budget = budgetVirtuelRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("BudgetVirtuel not found with trackingId: " + trackingId));
        budgetVirtuelRepository.delete(budget);
    }

    @Override
    public BudgetVirtuel verifierEtDebiterBudget(UUID merchantTrackingId, Double montant) {
        // F10 - Recupere le commercant par trackingId
        Merchant merchant = merchantRepository.findByTrackingId(merchantTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found: " + merchantTrackingId));

        // Determine le mois en cours (format YYYY-MM)
        LocalDate aujourd_hui = LocalDate.now();
        String moisActuel = String.format("%04d-%02d", aujourd_hui.getYear(), aujourd_hui.getMonthValue());

        // Recupere le budget actif pour ce mois
        BudgetVirtuel budget = budgetVirtuelRepository.findByMerchantIdAndPeriode(merchant.getId(), moisActuel)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active budget for merchant " + merchantTrackingId + " in period " + moisActuel));

        // Verifie que le budget n'est pas epuise
        if (budget.getEstEpuise()) {
            throw new IllegalStateException("Budget boutique epuise");
        }

        // Verifie que le budget est suffisant
        if (budget.getMontantRestant() < montant) {
            throw new IllegalStateException("Budget insuffisant");
        }

        // Debite le montant du budget
        budget.setMontantRestant(budget.getMontantRestant() - montant);

        // Marque comme epuise si le solde restant <= 0
        if (budget.getMontantRestant() <= 0) {
            budget.setEstEpuise(true);
        }

        // Sauvegarde et retourne le budget mis a jour
        return budgetVirtuelRepository.save(budget);
    }

    @Override
    public BudgetVirtuelResponse allocuerBudget(BudgetVirtuelRequest request) {
        // F9 - Recupere le commercant par trackingId
        Merchant merchant = merchantRepository.findByTrackingId(request.merchantTrackingId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found: " + request.merchantTrackingId()));

        // Verifie qu'il n'existe pas deja un budget pour ce mois
        budgetVirtuelRepository.findByMerchantIdAndPeriode(merchant.getId(), request.periodeMois())
                .ifPresent(existingBudget -> {
                    throw new AlreadyExistException(
                            "Budget already exists for merchant " + request.merchantTrackingId() +
                            " in period " + request.periodeMois());
                });

        // Cree le budget avec mapper
        BudgetVirtuel budget = budgetVirtuelMapper.toEntity(request);

        // Sauvegarde et retourne
        BudgetVirtuel savedBudget = budgetVirtuelRepository.save(budget);
        return budgetVirtuelMapper.toResponse(savedBudget);
    }
}
