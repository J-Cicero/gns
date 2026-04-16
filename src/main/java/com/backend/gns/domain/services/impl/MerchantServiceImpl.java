package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.MerchantRequest;
import com.backend.gns.application.dtos.responses.MerchantResponse;
import com.backend.gns.application.mappers.MerchantMapper;
import com.backend.gns.domain.models.Merchant;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.infrastructure.repositories.MerchantRepository;
import com.backend.gns.domain.services.MerchantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;

    public MerchantServiceImpl(MerchantRepository merchantRepository, MerchantMapper merchantMapper) {
        this.merchantRepository = merchantRepository;
        this.merchantMapper = merchantMapper;
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
        merchant.setNomBoutique(request.nomBoutique());
        merchant.setCategorieShop(request.categorieShop());
        merchant.setStatutKYC(request.statutKYC());
        
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
    public List<MerchantResponse> findByStatutKYC(KycStatus statutKYC) {
        return merchantRepository.findByStatutKYC(statutKYC).stream()
                .map(merchantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchantResponse> findAll() {
        return merchantRepository.findAll().stream()
                .map(merchantMapper::toResponse)
                .toList();
    }
}
