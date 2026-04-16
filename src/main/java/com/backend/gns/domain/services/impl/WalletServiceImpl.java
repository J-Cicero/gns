package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.WalletRequest;
import com.backend.gns.application.dtos.responses.WalletResponse;
import com.backend.gns.application.mappers.WalletMapper;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.domain.enums.WalletType;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import com.backend.gns.domain.services.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    public WalletServiceImpl(WalletRepository walletRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
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
                .orElseThrow(() -> new EntityNotFoundException("Portefeuille non trouvé avec l'ID: " + trackingId));
        
        wallet.setTypeWallet(request.typeWallet());
        wallet.setSolde(request.solde().doubleValue());
        wallet.setPlafond(request.plafond().doubleValue());
        wallet.setEstVerrouille(request.estVerrouille());
        
        Wallet updatedWallet = walletRepository.save(wallet);
        return walletMapper.toResponse(updatedWallet);
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        Wallet wallet = walletRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Portefeuille non trouvé avec l'ID: " + trackingId));
        walletRepository.delete(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WalletResponse> findByStudentTrackingId(UUID studentTrackingId) {
        return walletRepository.findByStudentTrackingId(studentTrackingId)
                .map(walletMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletResponse> findByTypeWallet(WalletType typeWallet) {
        return walletRepository.findByTypeWallet(typeWallet).stream()
                .map(walletMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletResponse> findByEstVerrouille(Boolean estVerrouille) {
        return walletRepository.findByEstVerrouille(estVerrouille).stream()
                .map(walletMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletResponse> findAll() {
        return walletRepository.findAll().stream()
                .map(walletMapper::toResponse)
                .toList();
    }
}
