package com.backend.gns.core.parametrage.domain.services;

import com.backend.gns.core.parametrage.application.dtos.requests.CompteBancaireRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.CompteBancaireResponse;
import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompteBancaireService {
    CompteBancaireResponse createAccount(UUID ownerTrackingId, CompteBancaireRequest request);
    CompteBancaireResponse uploadRib(UUID compteTrackingId, MultipartFile file);
    void uploadMandat(UUID compteTrackingId, MultipartFile file);
    List<CompteBancaireResponse> findAll();
    Optional<CompteBancaireResponse> findByOwnerTrackingId(UUID ownerTrackingId);
    void delete(UUID trackingId);
}