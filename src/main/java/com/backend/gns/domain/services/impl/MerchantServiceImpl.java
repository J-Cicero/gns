package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.domain.dtos.requests.MerchantRequest;
import com.backend.gns.domain.dtos.responses.MerchantResponse;
import com.backend.gns.domain.mappers.MerchantMapper;
import com.backend.gns.domain.models.Merchant;
import com.backend.gns.infrastructure.repositories.MerchantRepository;
import com.backend.gns.domain.services.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;

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
        return merchantMapper.toResponse(merchant);
    }

    @Override
    public MerchantResponse update(UUID trackingId, MerchantRequest request) {
        Merchant merchant = merchantRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found with trackingId: " + trackingId));

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
}
