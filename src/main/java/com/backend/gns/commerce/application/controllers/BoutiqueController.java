package com.backend.gns.application.controllers;

import com.backend.gns.application.dtos.requests.BoutiqueRequest;
import com.backend.gns.application.dtos.responses.BoutiqueResponse;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.domain.services.BoutiqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boutiques")
@Tag(name = "BOUTIQUE", description = "Gestion des boutiques")
@CrossOrigin("*")
public class BoutiqueController {

	private final BoutiqueService boutiqueService;

	public BoutiqueController(BoutiqueService boutiqueService) {
		this.boutiqueService = boutiqueService;
	}

	@PostMapping
	@Operation(summary = "Créer une boutique", description = "Crée une nouvelle boutique")
	@ApiResponse(responseCode = "201", description = "Boutique créée avec succès")
	public ResponseEntity<?> create(@RequestBody BoutiqueRequest request) {
		try {
			BoutiqueResponse response = boutiqueService.create(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
		}
	}

	@GetMapping("/{trackingId}")
	@Operation(summary = "Récupérer une boutique", description = "Récupère une boutique par son ID")
	@ApiResponse(responseCode = "200", description = "Boutique trouvée")
	@ApiResponse(responseCode = "404", description = "Boutique non trouvée")
	public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
		try {
			return boutiqueService
					.findByTrackingId(trackingId)
					.map(response -> ResponseEntity.ok((Object) response))
					.orElse(
							ResponseEntity.status(HttpStatus.NOT_FOUND)
									.body(Map.of("error", "NOT_FOUND", "message", "Boutique non trouvée")));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
		}
	}

	@PutMapping("/{trackingId}")
	@Operation(
			summary = "Mettre à jour une boutique",
			description = "Mettre à jour les informations d'une boutique")
	@ApiResponse(responseCode = "200", description = "Boutique mise à jour avec succès")
	@ApiResponse(responseCode = "404", description = "Boutique non trouvée")
	public ResponseEntity<?> update(@PathVariable UUID trackingId, @RequestBody BoutiqueRequest request) {
		try {
			BoutiqueResponse response = boutiqueService.update(trackingId, request);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
		}
	}

	@DeleteMapping("/{trackingId}")
	@Operation(summary = "Supprimer une boutique", description = "Supprime une boutique par son ID")
	@ApiResponse(responseCode = "204", description = "Boutique supprimée avec succès")
	@ApiResponse(responseCode = "404", description = "Boutique non trouvée")
	public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
		try {
			boutiqueService.delete(trackingId);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
		}
	}

	@GetMapping("/merchant/{merchantTrackingId}")
	@Operation(
			summary = "Récupérer les boutiques d'un marchand",
			description = "Récupère toutes les boutiques liées à un marchand")
	@ApiResponse(responseCode = "200", description = "Boutiques trouvées")
	@ApiResponse(responseCode = "404", description = "Aucune boutique trouvée")
	public ResponseEntity<?> findByMerchantTrackingId(
			@PathVariable UUID merchantTrackingId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			var responses = boutiqueService.findByMerchantTrackingId(merchantTrackingId, pageable);
			if (!responses.hasContent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("error", "NOT_FOUND", "message", "Aucune boutique pour ce marchand"));
			}
			return ResponseEntity.ok(responses);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
		}
	}

	@GetMapping("/wallet/{walletTrackingId}")
	@Operation(
			summary = "Récupérer une boutique par portefeuille",
			description = "Récupère la boutique associée à un portefeuille")
	@ApiResponse(responseCode = "200", description = "Boutique trouvée")
	@ApiResponse(responseCode = "404", description = "Aucune boutique trouvée")
	public ResponseEntity<?> findByWalletTrackingId(@PathVariable UUID walletTrackingId) {
		try {
			return boutiqueService
					.findByWalletTrackingId(walletTrackingId)
					.map(response -> ResponseEntity.ok((Object) response))
					.orElse(
							ResponseEntity.status(HttpStatus.NOT_FOUND)
									.body(Map.of("error", "NOT_FOUND", "message", "Aucune boutique pour ce portefeuille")));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
		}
	}

	@GetMapping("/kyc/{statutKYC}")
	@Operation(
			summary = "Récupérer les boutiques par statut KYC",
			description = "Récupère toutes les boutiques avec un statut KYC donné")
	@ApiResponse(responseCode = "200", description = "Boutiques trouvées")
	@ApiResponse(responseCode = "404", description = "Aucune boutique trouvée")
	public ResponseEntity<?> findByStatutKYC(
			@PathVariable KycStatus statutKYC,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			var responses = boutiqueService.findByStatutKYC(statutKYC, pageable);
			if (!responses.hasContent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("error", "NOT_FOUND", "message", "Aucune boutique avec ce statut KYC"));
			}
			return ResponseEntity.ok(responses);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
		}
	}

	@GetMapping
	@Operation(
			summary = "Récupérer toutes les boutiques",
			description = "Récupère la liste de toutes les boutiques")
	@ApiResponse(responseCode = "200", description = "Boutiques récupérées avec succès")
	@ApiResponse(responseCode = "404", description = "Aucune boutique trouvée")
	public ResponseEntity<?> findAll(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			var responses = boutiqueService.findAll(pageable);
			if (!responses.hasContent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("error", "NOT_FOUND", "message", "Aucune boutique trouvée"));
			}
			return ResponseEntity.ok(responses);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
		}
	}
}
