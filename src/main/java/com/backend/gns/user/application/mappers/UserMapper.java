package com.backend.gns.user.application.mappers;

import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.user.application.dtos.requests.UserRequest;
import com.backend.gns.user.application.dtos.responses.UserResponse;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.user.domain.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final BoutiqueRepository boutiqueRepository;

  public User toEntity(UserRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("UserRequest cannot be null");
    }
    User user = new User();
    user.setTrackingId(UUID.randomUUID());
    user.setLastName(request.lastName());
    user.setFirstName(request.firstName());
    user.setEmail(request.email());
    user.setPhoneNumber(request.phoneNumber());
    if (request.role() != null && !request.role().trim().isEmpty()) {
      try {
        user.setRole(UserRole.valueOf(request.role()));
      } catch (IllegalArgumentException e) {
        user.setRole(UserRole.ETUDIANT);
      }
    } else {
      user.setRole(UserRole.ETUDIANT);
    }
    user.setActive(true);
    return user;
  }

  public UserResponse toResponse(User user) {
    if (user == null) {
      return null;
    }

    UUID walletTrackingId = null;
    String walletStatus = null;
    KycStatus kycStatus = user.getKycStatus();

    try {
      if (user instanceof Student student && student.getWallet() != null) {
        walletTrackingId = student.getWallet().getTrackingId();
        walletStatus = student.getWallet().getStatus() != null
            ? student.getWallet().getStatus().name()
            : null;
      } else if (user instanceof Merchant merchant) {
        List<Boutique> boutiques = boutiqueRepository.findByMerchant(merchant);
        if (boutiques != null && !boutiques.isEmpty()) {
          for (Boutique b : boutiques) {
            if (b.getWallet() != null && walletTrackingId == null) {
              walletTrackingId = b.getWallet().getTrackingId();
              walletStatus = b.getWallet().getStatus() != null
                  ? b.getWallet().getStatus().name()
                  : null;
            }
            if ((kycStatus == null || kycStatus == KycStatus.EN_ATTENTE) && b.getKycStatus() == KycStatus.VALIDE) {
              kycStatus = KycStatus.VALIDE;
            }
          }
        }
      }
    } catch (Exception ignored) {
    }

    return new UserResponse(
        user.getTrackingId(),
        user.getLastName(),
        user.getFirstName(),
        user.getPhoneNumber(),
        user.getEmail(),
        user.getRole() != null ? user.getRole().name() : null,
        user.getRegistrationDate(),
        kycStatus,
        user.isActive(),
        walletTrackingId,
        walletStatus);
  }
}
