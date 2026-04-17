package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.MerchantRequest;
import com.backend.gns.application.dtos.responses.MerchantResponse;
import com.backend.gns.application.mappers.MerchantMapper;
import com.backend.gns.domain.models.Merchant;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.infrastructure.repositories.MerchantRepository;
import com.backend.gns.domain.services.MerchantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class MerchantServiceImpl implements MerchantService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;

    public MerchantServiceImpl(MerchantRepository merchantRepository, MerchantMapper merchantMapper) {
        this.merchantRepository = merchantRepository;
        this.merchantMapper = merchantMapper;
    }

    private Pageable normalize(Pageable pageable) {
        int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }

    @Override
    @Transactional
    public MerchantResponse create(MerchantRequest request) {
        Merchant merchant = merchantMapper.toEntity(request);
        Merchant savedMerchant = merchantRepository.save(merchant);
        return merchantMapper.toResponse(savedMerchant);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MerchantResponse> findByTrackingId(UUID trackingId) {
        return merchantRepository.findByTrackingId(trackingId)
                .map(merchantMapper::toResponse);
    }

    @Override
    @Transactional
    public MerchantResponse update(UUID trackingId, MerchantRequest request) {
        Merchant merchant = merchantRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Commerçant non trouvé avec l'ID: " + trackingId));
        
        merchant.setEmail(request.email());
        merchant.setNom(request.nom());
        merchant.setPrenom(request.prenom());
        merchant.setTelephone(request.telephone());
        merchant.setDateNaissance(request.dateNaissance());
        
        Merchant updatedMerchant = merchantRepository.save(merchant);
        return merchantMapper.toResponse(updatedMerchant);
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        Merchant merchant = merchantRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Commerçant non trouvé avec l'ID: " + trackingId));
        merchantRepository.delete(merchant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MerchantResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable) {
        return merchantRepository.findByStatutKYCOrderByCreatedAtAsc(statutKYC, normalize(pageable))
                .map(merchantMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MerchantResponse> findAll(Pageable pageable) {
        return merchantRepository.findAll(normalize(pageable))
                .map(merchantMapper::toResponse);
    }
}
