package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.student.domain.services.StudentWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/students/workflow")
@Tag(name = "STUDENT WORKFLOW", description = "Orchestration du cycle de vie étudiant")
@RequiredArgsConstructor
public class StudentWorkflowController {

    private final StudentWorkflowService workflowService;

    @PostMapping("/validate-enrollment/{inscriptionTrackingId}")
    @Operation(summary = "Valider et Activer une inscription", 
               description = "Déclenche l'éligibilité et active le portefeuille de l'étudiant si éligible")
    public ResponseEntity<InscriptionAnnuelleResponse> validerEtActiver(@PathVariable UUID inscriptionTrackingId) {
        return ResponseEntity.ok(workflowService.validerEtActiverInscription(inscriptionTrackingId));
    }
}
