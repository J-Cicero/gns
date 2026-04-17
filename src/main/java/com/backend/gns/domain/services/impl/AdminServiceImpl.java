package com.backend.gns.domain.services.impl;

import com.backend.gns.application.dtos.requests.AdminRequest;
import com.backend.gns.application.dtos.responses.AdminResponse;
import com.backend.gns.application.mappers.AdminMapper;
import com.backend.gns.domain.models.Admin;
import com.backend.gns.domain.services.AdminService;
import com.backend.gns.infrastructure.repositories.AdminRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final AdminRepository adminRepository;
  private final AdminMapper adminMapper;

  public AdminServiceImpl(AdminRepository adminRepository, AdminMapper adminMapper) {
    this.adminRepository = adminRepository;
    this.adminMapper = adminMapper;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
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
    return adminRepository.findByTrackingId(trackingId).map(adminMapper::toResponse);
  }

  @Override
  @Transactional
  public AdminResponse update(UUID trackingId, AdminRequest request) {
    Admin admin =
        adminRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Admin non trouvé avec l'ID: " + trackingId));

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
    Admin admin =
        adminRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Admin non trouvé avec l'ID: " + trackingId));
    adminRepository.delete(admin);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AdminResponse> findAll(Pageable pageable) {
    return adminRepository.findAll(normalize(pageable)).map(adminMapper::toResponse);
  }
}
