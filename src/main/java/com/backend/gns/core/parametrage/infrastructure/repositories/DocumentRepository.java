package com.backend.gns.core.parametrage.infrastructure.repositories;

import com.backend.gns.core.parametrage.domain.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByTrackingId(UUID trackingId);


    @Query("SELECT d FROM Document d WHERE " +
            "(TYPE(d) = DocumentEtudiant AND d.student.trackingId = :ownerId) OR " +
            "(TYPE(d) = DocumentBanque AND d.compteBancaire.trackingId = :ownerId)")
    List<Document> findByOwnerTrackingId(@Param("ownerId") UUID ownerId);

    void deleteByTrackingId(UUID trackingId);
}