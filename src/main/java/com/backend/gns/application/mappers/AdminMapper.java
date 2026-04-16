package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.AdminRequest;
import com.backend.gns.application.dtos.responses.AdminResponse;
import com.backend.gns.domain.models.Admin;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdminMapper {

    public Admin toEntity(AdminRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête AdminRequest ne peut pas être nulle");
        }

        Admin admin = new Admin();
        admin.setTrackingId(UUID.randomUUID());
        admin.setEmail(request.email());
        admin.setPassword(request.password());
        admin.setNom(request.nom());
        admin.setPrenom(request.prenom());
        admin.setRole(request.role());
        admin.setEstActif(request.estActif());
        admin.setTelephone(request.telephone());
        admin.setDateNaissance(request.dateNaissance());
        admin.setNumeroCompte(request.numeroCompte());

        return admin;
    }

    public AdminResponse toResponse(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("L'entité Admin ne peut pas être nulle");
        }

        return AdminResponse.builder()
                .trackingId(admin.getTrackingId())
                .email(admin.getEmail())
                .nom(admin.getNom())
                .prenom(admin.getPrenom())
                .role(admin.getRole())
                .estActif(admin.isEstActif())
                .telephone(admin.getTelephone())
                .dateNaissance(admin.getDateNaissance())
                .numeroCompte(admin.getNumeroCompte())
                .build();
    }

    public Admin toEntityFromResponse(AdminResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("La réponse AdminResponse ne peut pas être nulle");
        }

        Admin admin = new Admin();
        admin.setTrackingId(response.trackingId());
        admin.setEmail(response.email());
        admin.setNom(response.nom());
        admin.setPrenom(response.prenom());
        admin.setRole(response.role());
        admin.setEstActif(response.estActif());
        admin.setTelephone(response.telephone());
        admin.setDateNaissance(response.dateNaissance());
        admin.setNumeroCompte(response.numeroCompte());

        return admin;
    }

}
