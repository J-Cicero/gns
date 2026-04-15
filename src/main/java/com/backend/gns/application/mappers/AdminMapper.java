package com.backend.gns.application.mappers;

import com.backend.gns.Shared.user.domain.enums.TypeRole;
import com.backend.gns.domain.dtos.requests.AdminRequest;
import com.backend.gns.domain.dtos.responses.AdminResponse;
import com.backend.gns.domain.models.Admin;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AdminMapper {

    private final PasswordEncoder passwordEncoder;

    public AdminMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Admin toEntity(AdminRequest request) {
        if (request == null) {
            return null;
        }

        Admin admin = new Admin();
        admin.setTrackingId(UUID.randomUUID());
        admin.setNom(request.nom());
        admin.setPrenom(request.prenom());
        admin.setEmail(request.email());
        admin.setPassword(passwordEncoder.encode(request.motDePasse()));
        admin.setTelephone(request.telephone());
        admin.setGrade(request.grade());
        admin.setRole(TypeRole.ADMIN);
        admin.setEstActif(true);

        return admin;
    }

    public AdminResponse toResponse(Admin entity) {
        if (entity == null) {
            return null;
        }

        return new AdminResponse(
                entity.getTrackingId(),
                entity.getNom(),
                entity.getPrenom(),
                entity.getEmail(),
                entity.getTelephone(),
                entity.getDateInscription(),
                entity.isEstActif(),
                entity.getGrade(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<AdminResponse> toResponseList(List<Admin> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
