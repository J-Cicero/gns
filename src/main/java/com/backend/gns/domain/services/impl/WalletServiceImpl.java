package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.domain.dtos.requests.WalletRequest;
import com.backend.gns.domain.dtos.responses.WalletResponse;
import com.backend.gns.domain.mappers.WalletMapper;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import com.backend.gns.domain.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Override
    public WalletResponse create(WalletRequest request) {
        Wallet wallet = walletMapper.toEntity(request);
        Wallet savedWallet = walletRepository.save(wallet);
        return walletMapper.toResponse(savedWallet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletResponse> getAll() {
        List<Wallet> wallets = walletRepository.findAll();
        return walletMapper.toResponseList(wallets);
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getByTrackingId(UUID trackingId) {
        Wallet wallet = walletRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with trackingId: " + trackingId));
        return walletMapper.toResponse(wallet);
    }

    @Override
    public WalletResponse update(UUID trackingId, WalletRequest request) {
        Wallet wallet = walletRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with trackingId: " + trackingId));

        wallet.setTypeWallet(com.backend.gns.domain.enums.WalletType.valueOf(request.typeWallet()));
        wallet.setSolde(request.solde());
        wallet.setPlafond(request.plafond());

        Wallet updatedWallet = walletRepository.save(wallet);
        return walletMapper.toResponse(updatedWallet);
    }

    @Override
    public void delete(UUID trackingId) {
        Wallet wallet = walletRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with trackingId: " + trackingId));
        walletRepository.delete(wallet);
    }
}
