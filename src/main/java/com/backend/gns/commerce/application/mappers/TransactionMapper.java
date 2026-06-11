package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.responses.TransactionResponse;
import com.backend.gns.commerce.domain.models.Transaction;
import com.backend.gns.commerce.domain.services.BoutiqueService;
import com.backend.gns.student.domain.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final StudentService studentService;
    private final BoutiqueService boutiqueService;

    public TransactionResponse toResponse(Transaction entity) {
        String studentName = studentService.findByTrackingId(entity.getStudent().getTrackingId())
                .map(s -> s.nom() + " " + s.prenom())
                .orElse("Inconnu");

        String boutiqueName = boutiqueService.findByTrackingId(entity.getBoutique().getTrackingId())
                .map(b -> b.nomBoutique())
                .orElse("Inconnu");

        return new TransactionResponse(
            entity.getTrackingId(),
            studentName,
            boutiqueName,
            entity.getMontantDebite(),
            entity.getMontantNetBoutique(),
            entity.getCommissionTotale(),
            entity.getDate(),
            entity.getStatut()
        );
    }
}
