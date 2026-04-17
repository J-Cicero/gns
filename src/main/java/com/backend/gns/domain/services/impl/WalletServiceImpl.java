package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.WalletRequest;
import com.backend.gns.application.dtos.responses.WalletResponse;
import com.backend.gns.application.mappers.WalletMapper;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.domain.enums.WalletStatus;
import com.backend.gns.domain.enums.WalletType;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import com.backend.gns.domain.services.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    public WalletServiceImpl(WalletRepository walletRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

    private Pageable normalize(Pageable pageable) {
        int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }

    @Override
    @Transactional
    public WalletResponse create(WalletRequest request) {
        Wallet wallet = walletMapper.toEntity(request);
        Wallet savedWallet = walletRepository.save(wallet);
        return walletMapper.toResponse(savedWallet);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WalletResponse> findByTrackingId(UUID trackingId) {
        return walletRepository.findByTrackingId(trackingId)
                .map(walletMapper::toResponse);
    }

    @Override
    @Transactional
    public WalletResponse update(UUID trackingId, WalletRequest request) {
        Wallet wallet = walletRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Portefeuille non trouvé avec l'ID: " + trackingId));

        wallet.setTypeWallet(request.typeWallet());
        wallet.setStatutWallet(request.statutWallet());
        wallet.setSolde(request.solde());
        wallet.setPlafond(request.plafond());
        wallet.setEstVerrouille(request.estVerrouille());

        return walletMapper.toResponse(walletRepository.save(wallet));
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        Wallet wallet = walletRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Portefeuille non trouvé avec l'ID: " + trackingId));
        walletRepository.delete(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletResponse> findByTypeWallet(WalletType typeWallet, Pageable pageable) {
        return walletRepository.findByTypeWallet(typeWallet, normalize(pageable))
                .map(walletMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletResponse> findByEstVerrouille(Boolean estVerrouille, Pageable pageable) {
        return walletRepository.findByEstVerrouille(estVerrouille, normalize(pageable))
                .map(walletMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletResponse> findByStatutWallet(WalletStatus statutWallet, Pageable pageable) {
        return walletRepository.findByStatutWallet(statutWallet, normalize(pageable))
                .map(walletMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletResponse> findBySoldeLessThan(BigDecimal amount, Pageable pageable) {
        return walletRepository.findBySoldeLessThan(amount, normalize(pageable))
                .map(walletMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletResponse> findBySoldeGreaterThan(BigDecimal amount, Pageable pageable) {
        return walletRepository.findBySoldeGreaterThan(amount, normalize(pageable))
                .map(walletMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletResponse> findAll(Pageable pageable) {
        return walletRepository.findAll(normalize(pageable))
                .map(walletMapper::toResponse);
    }

    @Override
    @Transactional
    public void crediter(UUID walletTrackingId, BigDecimal montant) {
        Wallet wallet = walletRepository.findByTrackingId(walletTrackingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Portefeuille non trouvé avec l'ID: " + walletTrackingId));
        wallet.setSolde(wallet.getSolde().add(montant));
        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void debiter(UUID walletTrackingId, BigDecimal montant) {
        Wallet wallet = walletRepository.findByTrackingId(walletTrackingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Portefeuille non trouvé avec l'ID: " + walletTrackingId));
        if (wallet.getSolde().compareTo(montant) < 0) {
            throw new IllegalArgumentException("Solde insuffisant. Solde: " + wallet.getSolde() + ", Montant: " + montant);
        }
        wallet.setSolde(wallet.getSolde().subtract(montant));
        walletRepository.save(wallet);
    }
}
