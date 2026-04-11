package com.backend.gns.domain.mappers;

import com.backend.gns.domain.dtos.requests.BudgetVirtuelRequest;
import com.backend.gns.domain.dtos.responses.BudgetVirtuelResponse;
import com.backend.gns.domain.models.BudgetVirtuel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BudgetVirtuelMapper {

    public BudgetVirtuel toEntity(BudgetVirtuelRequest request) {
        if (request == null) {
            return null;
        }

        BudgetVirtuel budget = new BudgetVirtuel();
        budget.setTrackingId(UUID.randomUUID());
        budget.setMerchantTrackingId(request.merchantTrackingId());
        budget.setMontantAlloue(request.montantAlloue());
        budget.setMontantRestant(request.montantAlloue());
        budget.setPeriodeMois(request.periodeMois());
        budget.setEstEpuise(false);

        return budget;
    }

    public BudgetVirtuelResponse toResponse(BudgetVirtuel entity) {
        if (entity == null) {
            return null;
        }

        return new BudgetVirtuelResponse(
                entity.getTrackingId(),
                entity.getMerchantTrackingId(),
                entity.getMontantAlloue(),
                entity.getMontantRestant(),
                entity.getPeriodeMois(),
                entity.getEstEpuise(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<BudgetVirtuelResponse> toResponseList(List<BudgetVirtuel> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
