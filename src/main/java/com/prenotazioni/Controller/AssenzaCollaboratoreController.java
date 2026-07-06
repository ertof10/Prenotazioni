package com.prenotazioni.Controller;

import com.prenotazioni.Dto.AssenzaCollaboratoreTo;
import com.prenotazioni.Response.EsitoResponse;
import com.prenotazioni.Service.AssenzaCollaboratoreService;
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

    @GetMapping("/cerca-per-id/{idCollaboratore}")
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