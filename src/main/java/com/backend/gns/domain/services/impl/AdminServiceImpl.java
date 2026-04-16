package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.AdminRequest;
import com.backend.gns.application.dtos.responses.AdminResponse;
import com.backend.gns.application.mappers.AdminMapper;
import com.backend.gns.domain.models.Admin;
import com.backend.gns.infrastructure.repositories.AdminRepository;
import com.backend.gns.domain.services.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;

    public AdminServiceImpl(AdminRepository adminRepository, AdminMapper adminMapper) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
    }

    @Override
    @Transactional
    public AdminResponse create(AdminRequest request) {
        Admin admin = adminMapper.toEntity(request);
        Admin savedAdmin = adminRepository.save(admin);
        return adminMapper.toResponse(savedAdmin);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AdminResponse> findByTrackingId(UUID trackingId) {
        return adminRepository.findByTrackingId(trackingId)
                .map(adminMapper::toResponse);
    }

    @Override
    @Transactional
    public AdminResponse update(UUID trackingId, AdminRequest request) {
        Admin admin = adminRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Admin non trouvé avec l'ID: " + trackingId));
        
        admin.setEmail(request.email());
        admin.setNom(request.nom());
        admin.setPrenom(request.prenom());
        admin.setTelephone(request.telephone());
        admin.setDateNaissance(request.dateNaissance());
        admin.setNumeroCompte(request.numeroCompte());
        
        Admin updatedAdmin = adminRepository.save(admin);
        return adminMapper.toResponse(updatedAdmin);
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        Admin admin = adminRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Admin non trouvé avec l'ID: " + trackingId));
        adminRepository.delete(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminResponse> findAll() {
        return adminRepository.findAll().stream()
                .map(adminMapper::toResponse)
                .toList();
    }
}
