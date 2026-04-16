package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Wallet;
import com.backend.gns.domain.enums.WalletType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query("SELECT w FROM Wallet w WHERE w.trackingId = :trackingId")
    Optional<Wallet> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT w FROM Wallet w WHERE w.student.trackingId = :studentTrackingId")
    Optional<Wallet> findByStudentTrackingId(@Param("studentTrackingId") UUID studentTrackingId);

    @Query("SELECT w FROM Wallet w WHERE w.student.trackingId = :studentTrackingId AND w.typeWallet = :type")
    Optional<Wallet> findByStudentTrackingIdAndType(@Param("studentTrackingId") UUID studentTrackingId, 
                                           @Param("type") WalletType type);

    @Query("SELECT w FROM Wallet w WHERE w.typeWallet = :typeWallet")
    List<Wallet> findByTypeWallet(@Param("typeWallet") WalletType typeWallet);

    @Query("SELECT w FROM Wallet w WHERE w.estVerrouille = :estVerrouille")
    List<Wallet> findByEstVerrouille(@Param("estVerrouille") Boolean estVerrouille);
}
