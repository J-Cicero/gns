package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.BoutiqueRequest;
import com.backend.gns.application.dtos.responses.BoutiqueResponse;
import com.backend.gns.application.mappers.BoutiqueMapper;
import com.backend.gns.domain.models.Boutique;
import com.backend.gns.domain.models.Merchant;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.infrastructure.repositories.MerchantRepository;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import com.backend.gns.domain.services.BoutiqueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class BoutiqueServiceImpl implements BoutiqueService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final BoutiqueRepository boutiqueRepository;
    private final BoutiqueMapper boutiqueMapper;
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;

    public BoutiqueServiceImpl(BoutiqueRepository boutiqueRepository, BoutiqueMapper boutiqueMapper, 
                             MerchantRepository merchantRepository, WalletRepository walletRepository) {
        this.boutiqueRepository = boutiqueRepository;
        this.boutiqueMapper = boutiqueMapper;
        this.merchantRepository = merchantRepository;
        this.walletRepository = walletRepository;
    }

    private Pageable normalize(Pageable pageable) {
        int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }

    @Override
    @Transactional
    public BoutiqueResponse create(BoutiqueRequest request) {
        Boutique boutique = boutiqueMapper.toEntity(request);
        
        if (request.merchantTrackingId() != null) {
            Merchant merchant = merchantRepository.findByTrackingId(request.merchantTrackingId())
                    .orElseThrow(() -> new EntityNotFoundException("Commerçant non trouvé avec l'ID: " + request.merchantTrackingId()));
            boutique.setMerchant(merchant);
        }
        
        if (request.walletTrackingId() != null) {
            Wallet wallet = walletRepository.findByTrackingId(request.walletTrackingId())
                    .orElseThrow(() -> new EntityNotFoundException("Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
            boutique.setWallet(wallet);
        }
        
        Boutique savedBoutique = boutiqueRepository.save(boutique);
        return boutiqueMapper.toResponse(savedBoutique);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BoutiqueResponse> findByTrackingId(UUID trackingId) {
        return boutiqueRepository.findByTrackingId(trackingId)
                .map(boutiqueMapper::toResponse);
    }

    @Override
    @Transactional
    public BoutiqueResponse update(UUID trackingId, BoutiqueRequest request) {
        Boutique boutique = boutiqueRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Boutique non trouvée avec l'ID: " + trackingId));
        
        boutique.setNomBoutique(request.nomBoutique());
        boutique.setCategorieShop(request.categorieShop());
        boutique.setStatutKYC(request.statutKYC());
        boutique.setLatitude(request.latitude());
        boutique.setLongitude(request.longitude());
        
        if (request.merchantTrackingId() != null) {
            Merchant merchant = merchantRepository.findByTrackingId(request.merchantTrackingId())
                    .orElseThrow(() -> new EntityNotFoundException("Commerçant non trouvé avec l'ID: " + request.merchantTrackingId()));
            boutique.setMerchant(merchant);
        }
        
        if (request.walletTrackingId() != null) {
            Wallet wallet = walletRepository.findByTrackingId(request.walletTrackingId())
                    .orElseThrow(() -> new EntityNotFoundException("Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
            boutique.setWallet(wallet);
        }
        
        Boutique updatedBoutique = boutiqueRepository.save(boutique);
        return boutiqueMapper.toResponse(updatedBoutique);
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        Boutique boutique = boutiqueRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Boutique non trouvée avec l'ID: " + trackingId));
        boutiqueRepository.delete(boutique);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoutiqueResponse> findByMerchantTrackingId(UUID merchantTrackingId, Pageable pageable) {
        return boutiqueRepository.findByMerchantTrackingId(merchantTrackingId, normalize(pageable))
                .map(boutiqueMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BoutiqueResponse> findByWalletTrackingId(UUID walletTrackingId) {
        return boutiqueRepository.findByWalletTrackingId(walletTrackingId)
                .map(boutiqueMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoutiqueResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable) {
        return boutiqueRepository.findByStatutKYC(statutKYC, normalize(pageable))
                .map(boutiqueMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoutiqueResponse> findAll(Pageable pageable) {
        return boutiqueRepository.findAll(normalize(pageable))
                .map(boutiqueMapper::toResponse);
    }
}