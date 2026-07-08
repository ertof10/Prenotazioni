package com.prenotazioni.controller;

import com.prenotazioni.dto.CollaboratoreTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.CollaboratoreService;
import com.prenotazioni.validation.ValidationGroups;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping("/collaboratore")
public class CollaboratoreController {

    private final CollaboratoreService collaboratoreService;

    public CollaboratoreController(CollaboratoreService collaboratoreService) {
        this.collaboratoreService = collaboratoreService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<CollaboratoreTo> inserimentoCollaboratore(
            @Validated(ValidationGroups.Create.class) @RequestBody CollaboratoreTo collaboratoreTo) {

        return ResponseEntity.ok(collaboratoreService.saveOrUpdateCollaboratore(collaboratoreTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<CollaboratoreTo> modificaCollaboratore(
            @Validated(ValidationGroups.Update.class) @RequestBody CollaboratoreTo collaboratoreTo) {

        return ResponseEntity.ok(collaboratoreService.saveOrUpdateCollaboratore(collaboratoreTo));
    }

    @PutMapping("/disattiva/{idCollaboratore}")
    public ResponseEntity<CollaboratoreTo> disattivaCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        return ResponseEntity.ok(collaboratoreService.disattivaCollaboratore(idCollaboratore));
    }

    @PutMapping("/riattiva/{idCollaboratore}")
    public ResponseEntity<CollaboratoreTo> riattivaCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        return ResponseEntity.ok(collaboratoreService.riattivaCollaboratore(idCollaboratore));
    }

    @GetMapping("/cerca-per-id/{idCollaboratore}")
    public ResponseEntity<CollaboratoreTo> cercaCollaboratorePerId(
            @PathVariable @Min(1) Integer idCollaboratore) {

        return ResponseEntity.ok(collaboratoreService.getCollaboratoreById(idCollaboratore));
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<List<CollaboratoreTo>> stampaTuttiCollaboratori() {
        return ResponseEntity.ok(collaboratoreService.getAllCollaboratori());
    }

    @GetMapping("/stampa-attivi")
    public ResponseEntity<List<CollaboratoreTo>> stampaCollaboratoriAttivi() {
        return ResponseEntity.ok(collaboratoreService.getCollaboratoriAttivi());
    }

    @DeleteMapping("/elimina-per-id/{idCollaboratore}")
    public ResponseEntity<EsitoResponse> eliminaCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        return ResponseEntity.ok(collaboratoreService.deleteCollaboratore(idCollaboratore));
    }
}