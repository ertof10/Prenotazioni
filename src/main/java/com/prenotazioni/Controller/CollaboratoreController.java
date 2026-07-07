package com.prenotazioni.Controller;

import com.prenotazioni.Dto.CollaboratoreTo;
import com.prenotazioni.Response.EsitoResponse;
import com.prenotazioni.Service.CollaboratoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/collaboratore")
public class CollaboratoreController {

    private final CollaboratoreService collaboratoreService;

    public CollaboratoreController(CollaboratoreService collaboratoreService) {
        this.collaboratoreService = collaboratoreService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<CollaboratoreTo> inserimentoCollaboratore(@Valid @RequestBody CollaboratoreTo collaboratoreTo) {
        return ResponseEntity.ok(collaboratoreService.saveOrUpdateCollaboratore(collaboratoreTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<CollaboratoreTo> modificaCollaboratore(@Valid @RequestBody CollaboratoreTo collaboratoreTo) {
        return ResponseEntity.ok(collaboratoreService.saveOrUpdateCollaboratore(collaboratoreTo));
    }

    @PutMapping("/disattiva/{idCollaboratore}")
    public ResponseEntity<CollaboratoreTo> disattivaCollaboratore(@PathVariable Integer idCollaboratore) {
        return ResponseEntity.ok(collaboratoreService.disattivaCollaboratore(idCollaboratore));
    }

    @PutMapping("/riattiva/{idCollaboratore}")
    public ResponseEntity<CollaboratoreTo> riattivaCollaboratore(@PathVariable Integer idCollaboratore) {
        return ResponseEntity.ok(collaboratoreService.riattivaCollaboratore(idCollaboratore));
    }

    @GetMapping("/cerca-per-id/{idCollaboratore}")
    public ResponseEntity<CollaboratoreTo> cercaCollaboratorePerId(@PathVariable Integer idCollaboratore) {
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
    public ResponseEntity<EsitoResponse> eliminaCollaboratore(@PathVariable Integer idCollaboratore) {
        return ResponseEntity.ok(collaboratoreService.deleteCollaboratore(idCollaboratore));
    }
}