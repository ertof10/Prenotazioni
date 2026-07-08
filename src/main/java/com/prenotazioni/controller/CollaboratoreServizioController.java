package com.prenotazioni.controller;

import com.prenotazioni.dto.CollaboratoreServizioTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.CollaboratoreServizioService;
import com.prenotazioni.validation.ValidationGroups;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping("/collaboratore-servizio")
public class CollaboratoreServizioController {

    private final CollaboratoreServizioService collaboratoreServizioService;

    public CollaboratoreServizioController(CollaboratoreServizioService collaboratoreServizioService) {
        this.collaboratoreServizioService = collaboratoreServizioService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<CollaboratoreServizioTo> inserimentoCollaboratoreServizio(
            @Validated(ValidationGroups.Create.class) @RequestBody CollaboratoreServizioTo collaboratoreServizioTo) {

        return ResponseEntity.ok(
                collaboratoreServizioService.saveOrUpdateCollaboratoreServizio(collaboratoreServizioTo)
        );
    }

    @PutMapping("/modifica")
    public ResponseEntity<CollaboratoreServizioTo> modificaCollaboratoreServizio(
            @Validated(ValidationGroups.Update.class) @RequestBody CollaboratoreServizioTo collaboratoreServizioTo) {

        return ResponseEntity.ok(
                collaboratoreServizioService.saveOrUpdateCollaboratoreServizio(collaboratoreServizioTo)
        );
    }

    @GetMapping("/cerca-per-id/{idCollaboratoreServizio}")
    public ResponseEntity<CollaboratoreServizioTo> cercaCollaboratoreServizioPerId(
            @PathVariable @Min(1) Integer idCollaboratoreServizio) {

        return ResponseEntity.ok(
                collaboratoreServizioService.getCollaboratoreServizioById(idCollaboratoreServizio)
        );
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<List<CollaboratoreServizioTo>> stampaTuttiCollaboratoriServizi() {
        return ResponseEntity.ok(collaboratoreServizioService.getAllCollaboratoriServizi());
    }

    @GetMapping("/stampa-servizi-per-collaboratore/{idCollaboratore}")
    public ResponseEntity<List<CollaboratoreServizioTo>> stampaServiziPerCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        return ResponseEntity.ok(
                collaboratoreServizioService.getServiziByCollaboratore(idCollaboratore)
        );
    }

    @GetMapping("/stampa-collaboratori-per-servizio/{idServizio}")
    public ResponseEntity<List<CollaboratoreServizioTo>> stampaCollaboratoriPerServizio(
            @PathVariable @Min(1) Integer idServizio) {

        return ResponseEntity.ok(
                collaboratoreServizioService.getCollaboratoriByServizio(idServizio)
        );
    }

    @DeleteMapping("/elimina-per-id/{idCollaboratoreServizio}")
    public ResponseEntity<EsitoResponse> eliminaCollaboratoreServizio(
            @PathVariable @Min(1) Integer idCollaboratoreServizio) {

        return ResponseEntity.ok(
                collaboratoreServizioService.deleteCollaboratoreServizio(idCollaboratoreServizio)
        );
    }
}