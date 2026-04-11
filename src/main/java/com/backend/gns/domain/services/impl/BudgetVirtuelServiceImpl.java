package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.domain.dtos.requests.BudgetVirtuelRequest;
import com.backend.gns.domain.dtos.responses.BudgetVirtuelResponse;
import com.backend.gns.domain.mappers.BudgetVirtuelMapper;
import com.backend.gns.domain.models.BudgetVirtuel;
import com.backend.gns.infrastructure.repositories.BudgetVirtuelRepository;
import com.backend.gns.domain.services.BudgetVirtuelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BudgetVirtuelServiceImpl implements BudgetVirtuelService {

    private final BudgetVirtuelRepository budgetVirtuelRepository;
    private final BudgetVirtuelMapper budgetVirtuelMapper;

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
}
