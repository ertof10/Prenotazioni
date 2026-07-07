package com.prenotazioni.controller;

import com.prenotazioni.dto.AssenzaCollaboratoreTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.AssenzaCollaboratoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assenze-collaboratori")
public class AssenzaCollaboratoreController {

    private final AssenzaCollaboratoreService assenzaCollaboratoreService;

    public AssenzaCollaboratoreController(AssenzaCollaboratoreService assenzaCollaboratoreService) {
        this.assenzaCollaboratoreService = assenzaCollaboratoreService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<AssenzaCollaboratoreTo> saveOrUpdateAssenzaCollaboratore(
            @RequestBody AssenzaCollaboratoreTo assenzaCollaboratoreTo) {

        return ResponseEntity.ok(
                assenzaCollaboratoreService.saveOrUpdateAssenzaCollaboratore(assenzaCollaboratoreTo)
        );
    }

    @GetMapping("/cerca-per-id/{idAssenzaCollaboratore}")
    public ResponseEntity<AssenzaCollaboratoreTo> getAssenzaCollaboratoreById(
            @PathVariable("idAssenzaCollaboratore") Integer idAssenzaCollaboratore) {

        return ResponseEntity.ok(
                assenzaCollaboratoreService.getAssenzaCollaboratoreById(idAssenzaCollaboratore)
        );
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<List<AssenzaCollaboratoreTo>> getAllAssenzeCollaboratori() {
        return ResponseEntity.ok(
                assenzaCollaboratoreService.getAllAssenzeCollaboratori()
        );
    }

    @GetMapping("/stampa-per-collaboratore/{idCollaboratore}")
    public ResponseEntity<List<AssenzaCollaboratoreTo>> getAssenzeByCollaboratore(
            @PathVariable("idCollaboratore") Integer idCollaboratore) {

        return ResponseEntity.ok(
                assenzaCollaboratoreService.getAssenzeByCollaboratore(idCollaboratore)
        );
    }

    @DeleteMapping("/elimina/{idAssenzaCollaboratore}")
    public ResponseEntity<EsitoResponse> deleteAssenzaCollaboratore(
            @PathVariable("idAssenzaCollaboratore") Integer idAssenzaCollaboratore) {

        return ResponseEntity.ok(
                assenzaCollaboratoreService.deleteAssenzaCollaboratore(idAssenzaCollaboratore)
        );
    }
}