package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.BudgetVirtuel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetVirtuelRepository extends JpaRepository<BudgetVirtuel, Long> {

    @Query("SELECT b FROM BudgetVirtuel b WHERE b.merchant.id = :merchantId")
    List<BudgetVirtuel> findByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT b FROM BudgetVirtuel b WHERE b.merchant.id = :merchantId AND b.periodeMois = :periode")
    Optional<BudgetVirtuel> findByMerchantIdAndPeriode(@Param("merchantId") Long merchantId, 
                                                       @Param("periode") String periode);

    @Query("SELECT b FROM BudgetVirtuel b WHERE b.trackingId = :trackingId")
    Optional<BudgetVirtuel> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT b FROM BudgetVirtuel b " +
           "WHERE b.merchant.trackingId = :trackingId " +
           "AND b.periodeMois = :periode")
    Optional<BudgetVirtuel> findByMerchantTrackingIdAndPeriode(
            @Param("trackingId") UUID trackingId,
            @Param("periode") String periode
    );
}
