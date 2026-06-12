package com.backend.gns.user.application.controllers;

import com.backend.gns.user.application.dtos.requests.UserRequest;
import com.backend.gns.user.application.dtos.responses.UserResponse;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.user.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admins/gns")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping("/create-banque-admin")
    // @PreAuthorize("hasRole('ADMIN_GNS')") // A activer quand la sécurité sera finalisée
    public ResponseEntity<UserResponse> createBanqueAdmin(@RequestBody UserRequest request) {
        // Forcer le rôle ADMIN_BANQUE et structurer correctement la request
        UserRequest adminBanqueRequest = new UserRequest(
                request.nom(),
                request.prenom(),
                request.telephone(),
                request.email(),
                request.motDePasse(),
                UserRole.ADMIN_BANQUE.name(),
                request.pays()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(adminBanqueRequest));
    }
}
