package com.backend.gns.domain.mappers;

import com.backend.gns.Shared.user.domain.models.User;
import com.backend.gns.Shared.user.domain.enums.TypeRole;
import com.backend.gns.domain.dtos.requests.MerchantRequest;
import com.backend.gns.domain.dtos.responses.MerchantResponse;
import com.backend.gns.domain.models.Merchant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MerchantMapper {

    private final PasswordEncoder passwordEncoder;

    public MerchantMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Merchant toEntity(MerchantRequest request) {
        if (request == null) {
            return null;
        }

        Merchant merchant = new Merchant();
        merchant.setTrackingId(UUID.randomUUID());
        merchant.setNom(request.nom());
        merchant.setPrenom(request.prenom());
        merchant.setEmail(request.email());
        merchant.setPassword(passwordEncoder.encode(request.motDePasse()));
        merchant.setTelephone(request.telephone());
        merchant.setNomBoutique(request.nomBoutique());
        merchant.setCheminCarteEDJ(request.cheminCarteEDJ());
        merchant.setCategorieShop(request.categorieShop());
        merchant.setRole(TypeRole.COMMERCANT);
        merchant.setEstActif(false);

        return merchant;
    }

    public MerchantResponse toResponse(Merchant entity) {
        if (entity == null) {
            return null;
        }

        return new MerchantResponse(
                entity.getTrackingId(),
                entity.getNom(),
                entity.getPrenom(),
                entity.getEmail(),
                entity.getTelephone(),
                entity.getDateInscription(),
                entity.isEstActif(),
                entity.getNomBoutique(),
                entity.getCheminCarteEDJ(),
                entity.getCategorieShop(),
                entity.getStatutKYC() != null ? entity.getStatutKYC().name() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<MerchantResponse> toResponseList(List<Merchant> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
