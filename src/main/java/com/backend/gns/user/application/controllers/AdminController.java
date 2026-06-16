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
    public ResponseEntity<UserResponse> createBanqueAdmin(@RequestBody com.backend.gns.user.application.dtos.requests.AdminBanqueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createAdminBanque(request));
    }

    @PostMapping("/create-gns-admin")
    public ResponseEntity<UserResponse> createGnsAdmin(@RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }
}
