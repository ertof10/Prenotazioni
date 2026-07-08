package com.prenotazioni.controller;

import com.prenotazioni.dto.AssenzaCollaboratoreTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.AssenzaCollaboratoreService;
import com.prenotazioni.validation.ValidationGroups;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping("/assenze-collaboratori")
public class AssenzaCollaboratoreController {

    private final AssenzaCollaboratoreService assenzaCollaboratoreService;

    public AssenzaCollaboratoreController(AssenzaCollaboratoreService assenzaCollaboratoreService) {
        this.assenzaCollaboratoreService = assenzaCollaboratoreService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<AssenzaCollaboratoreTo> inserimentoAssenzaCollaboratore(
            @Validated(ValidationGroups.Create.class) @RequestBody AssenzaCollaboratoreTo assenzaCollaboratoreTo) {

        return ResponseEntity.ok(
                assenzaCollaboratoreService.saveOrUpdateAssenzaCollaboratore(assenzaCollaboratoreTo)
        );
    }

    @PutMapping("/modifica")
    public ResponseEntity<AssenzaCollaboratoreTo> modificaAssenzaCollaboratore(
            @Validated(ValidationGroups.Update.class) @RequestBody AssenzaCollaboratoreTo assenzaCollaboratoreTo) {

        return ResponseEntity.ok(
                assenzaCollaboratoreService.saveOrUpdateAssenzaCollaboratore(assenzaCollaboratoreTo)
        );
    }

    @GetMapping("/cerca-per-id/{idAssenzaCollaboratore}")
    public ResponseEntity<AssenzaCollaboratoreTo> getAssenzaCollaboratoreById(
            @PathVariable @Min(1) Integer idAssenzaCollaboratore) {

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
            @PathVariable @Min(1) Integer idCollaboratore) {

        return ResponseEntity.ok(
                assenzaCollaboratoreService.getAssenzeByCollaboratore(idCollaboratore)
        );
    }

    @DeleteMapping("/elimina/{idAssenzaCollaboratore}")
    public ResponseEntity<EsitoResponse> deleteAssenzaCollaboratore(
            @PathVariable @Min(1) Integer idAssenzaCollaboratore) {

        return ResponseEntity.ok(
                assenzaCollaboratoreService.deleteAssenzaCollaboratore(idAssenzaCollaboratore)
        );
    }
}