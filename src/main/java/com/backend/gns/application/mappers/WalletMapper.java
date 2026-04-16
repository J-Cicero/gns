package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.WalletRequest;
import com.backend.gns.application.dtos.responses.WalletResponse;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.domain.models.Student;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class WalletMapper {

    private final WalletRepository walletRepository;
    private final StudentRepository studentRepository;

    public WalletMapper(WalletRepository walletRepository, 
                       StudentRepository studentRepository) {
        this.walletRepository = walletRepository;
        this.studentRepository = studentRepository;
    }

    public Wallet toEntity(WalletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête WalletRequest ne peut pas être nulle");
        }

        Wallet wallet = new Wallet();
        wallet.setTrackingId(UUID.randomUUID());
        wallet.setTypeWallet(request.typeWallet());
        wallet.setSolde(request.solde().doubleValue());
        wallet.setPlafond(request.plafond().doubleValue());
        wallet.setEstVerrouille(request.estVerrouille());
        wallet.setDateCreation(LocalDateTime.now());

        if (request.trackingStudentId() != null) {
            Student student = studentRepository.findByTrackingId(request.trackingStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé avec l'ID: " + request.trackingStudentId()));
            wallet.setStudent(student);
        }

        return wallet;
    }

    public WalletResponse toResponse(Wallet wallet) {
        if (wallet == null) {
            throw new IllegalArgumentException("L'entité Wallet ne peut pas être nulle");
        }

        return WalletResponse.builder()
                .trackingId(wallet.getTrackingId())
                .typeWallet(wallet.getTypeWallet())
                .solde(java.math.BigDecimal.valueOf(wallet.getSolde()))
                .plafond(java.math.BigDecimal.valueOf(wallet.getPlafond()))
                .estVerrouille(wallet.getEstVerrouille())
                .trackingStudentId(wallet.getStudent() != null ? wallet.getStudent().getTrackingId() : null)
                .build();
    }

    public Wallet toEntityFromResponse(WalletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("La réponse WalletResponse ne peut pas être nulle");
        }

        Wallet wallet = new Wallet();
        wallet.setTrackingId(response.trackingId());
        wallet.setTypeWallet(response.typeWallet());
        wallet.setSolde(response.solde().doubleValue());
        wallet.setPlafond(response.plafond().doubleValue());
        wallet.setEstVerrouille(response.estVerrouille());

        if (response.trackingStudentId() != null) {
            Student student = studentRepository.findByTrackingId(response.trackingStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé avec l'ID: " + response.trackingStudentId()));
            wallet.setStudent(student);
        }

        return wallet;
    }

}
