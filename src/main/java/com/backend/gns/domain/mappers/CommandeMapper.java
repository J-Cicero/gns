package com.backend.gns.domain.mappers;

import com.backend.gns.domain.dtos.requests.CommandeRequest;
import com.backend.gns.domain.dtos.responses.CommandeResponse;
import com.backend.gns.domain.enums.CommandeStatut;
import com.backend.gns.domain.models.Commande;
import com.backend.gns.domain.models.Merchant;
import com.backend.gns.domain.models.Student;
import com.backend.gns.infrastructure.repositories.MerchantRepository;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CommandeMapper {

    private final StudentRepository studentRepository;
    private final MerchantRepository merchantRepository;

    public CommandeMapper(StudentRepository studentRepository, MerchantRepository merchantRepository) {
        this.studentRepository = studentRepository;
        this.merchantRepository = merchantRepository;
    }

    public Commande toEntity(CommandeRequest request) {
        if (request == null) {
            return null;
        }

        Commande commande = new Commande();
        commande.setTrackingId(UUID.randomUUID());
        commande.setReference(UUID.randomUUID().toString());
        
        Student student = studentRepository.findByTrackingId(request.studentTrackingId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        commande.setStudent(student);
        
        Merchant merchant = merchantRepository.findByTrackingId(request.merchantTrackingId())
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));
        commande.setMerchant(merchant);
        
        commande.setMontantTotal(request.montantTotal());
        commande.setDateCommande(LocalDateTime.now());
        commande.setStatut(CommandeStatut.EN_COURS);

        return commande;
    }

    public CommandeResponse toResponse(Commande entity) {
        if (entity == null) {
            return null;
        }

        return new CommandeResponse(
                entity.getTrackingId(),
                entity.getReference(),
                entity.getStudent() != null ? entity.getStudent().getTrackingId() : null,
                entity.getMerchant() != null ? entity.getMerchant().getTrackingId() : null,
                entity.getMontantTotal(),
                entity.getDateCommande(),
                entity.getStatut().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<CommandeResponse> toResponseList(List<Commande> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
