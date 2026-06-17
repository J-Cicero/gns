package com.backend.gns.user.application.controllers;

import com.backend.gns.user.application.dtos.requests.UserRequest;
import com.backend.gns.user.application.dtos.responses.UserResponse;
import com.backend.gns.user.domain.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Tag(
    name = "UserController",
    description = "api permettant a un utilisateur de s'enregistrer et de se connecter ")
public class UserController {

  private final UserService userService;

  @Operation(
      summary = "Register a new user",
      description = "Register a new user and sends email if necessary")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or user already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PostMapping("/register")
  public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest request) {
    UserResponse response = userService.createUser(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping(value = "/register/student", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> registerStudent(
      @RequestPart("request") String requestJson,
      @RequestPart(value = "rib", required = false) org.springframework.web.multipart.MultipartFile rib,
      @RequestPart(value = "mandat", required = false) org.springframework.web.multipart.MultipartFile mandat) throws Exception {
      
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    com.backend.gns.student.application.dtos.requests.StudentRequest request = 
        mapper.readValue(requestJson, com.backend.gns.student.application.dtos.requests.StudentRequest.class);
        
    return ResponseEntity.ok(userService.registerStudent(request, rib, mandat));
  }

  @PostMapping(value = "/register/merchant", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> registerMerchant(
      @RequestPart("request") String requestJson,
      @RequestPart("rib") org.springframework.web.multipart.MultipartFile rib) throws Exception {
      
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    com.backend.gns.commerce.application.dtos.requests.MerchantRequest request = 
        mapper.readValue(requestJson, com.backend.gns.commerce.application.dtos.requests.MerchantRequest.class);
        
    return ResponseEntity.ok(userService.registerMerchant(request, rib));
  }

  @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token")
  @PostMapping("/login")
  public ResponseEntity<com.backend.gns.user.application.dtos.responses.LoginResponse> login(
      @RequestBody @Valid com.backend.gns.user.application.dtos.requests.LoginRequest request) {
    return ResponseEntity.ok(userService.login(request));
  }

  @GetMapping("/get/{trackingId}")
  @Operation(summary = "Get user by trackingId", description = "Get user by trackingId")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  public ResponseEntity<UserResponse> getUserByTrackingId(@PathVariable UUID trackingId) {
    return ResponseEntity.ok(userService.getUserByTrackingId(trackingId));
  }

  @PatchMapping("/etat/{trackingId}")
  @Operation(
      summary = "Update user activation status",
      description = "Activates or deactivates a user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  public ResponseEntity<UserResponse> updateUserEtat(
      @RequestParam boolean etat, @PathVariable UUID trackingId) {
    UserResponse response = userService.updateUserEtat(trackingId, etat);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Lister tous les utilisateurs",
      description = "Retourne la liste paginée de tous les utilisateurs")
  @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
  @GetMapping("/all")
  public ResponseEntity<Page<UserResponse>> getAllUsers(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    Page<UserResponse> allUsers = userService.getAllUsers(page, size);
    return ResponseEntity.ok(allUsers);
  }

  @DeleteMapping("/delete/{trackingId}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID trackingId) {
    userService.deleteUser(trackingId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/profile")
  @Operation(summary = "Get current user profile")
  public ResponseEntity<UserResponse> getProfile() {
    // In a real app we would get this from SecurityContext
    // For now we return the first one or a 404
    return userService.getAllUsers(0, 1).getContent().stream()
        .findFirst()
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/search")
  @Operation(
      summary = "Rechercher des utilisateurs",
      description = "Recherche des utilisateurs par nom, prénom ou email")
  @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès")
  public ResponseEntity<java.util.List<UserResponse>> searchUsers(@RequestParam String query) {
    return ResponseEntity.ok(userService.searchUsers(query));
  }
}
