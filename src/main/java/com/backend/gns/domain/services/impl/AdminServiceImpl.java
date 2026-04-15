package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.application.mappers.AdminMapper;
import com.backend.gns.domain.dtos.requests.AdminRequest;
import com.backend.gns.domain.dtos.responses.AdminResponse;
import com.backend.gns.domain.models.Admin;
import com.backend.gns.infrastructure.repositories.AdminRepository;
import com.backend.gns.domain.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;

    @Override
    public AdminResponse create(AdminRequest request) {
        Admin admin = adminMapper.toEntity(request);
        Admin savedAdmin = adminRepository.save(admin);
        return adminMapper.toResponse(savedAdmin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminResponse> getAll() {
        List<Admin> admins = adminRepository.findAll();
        return adminMapper.toResponseList(admins);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminResponse getByTrackingId(UUID trackingId) {
        Admin admin = adminRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with trackingId: " + trackingId));
        return adminMapper.toResponse(admin);
    }

    @Override
    public AdminResponse update(UUID trackingId, AdminRequest request) {
        Admin admin = adminRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with trackingId: " + trackingId));

        admin.setNom(request.nom());
        admin.setPrenom(request.prenom());
        admin.setEmail(request.email());
        admin.setMotDePasse(request.motDePasse());
        admin.setTelephone(request.telephone());
        admin.setGrade(request.grade());

        Admin updatedAdmin = adminRepository.save(admin);
        return adminMapper.toResponse(updatedAdmin);
    }

    @Override
    public void delete(UUID trackingId) {
        Admin admin = adminRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with trackingId: " + trackingId));
        adminRepository.delete(admin);
    }
}
