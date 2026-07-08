package com.prenotazioni.controller;

import com.prenotazioni.dto.UtenteTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.UtenteService;
import com.prenotazioni.validation.ValidationGroups;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping("/utente")
public class UtenteController {

    private final UtenteService service;

    public UtenteController(UtenteService service) {
        this.service = service;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<UtenteTo> inserimentoUtente(
            @Validated(ValidationGroups.Create.class) @RequestBody UtenteTo utenteTo) {

        return ResponseEntity.ok(service.saveOrUpdateUtente(utenteTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<UtenteTo> modificaUtente(
            @Validated(ValidationGroups.Update.class) @RequestBody UtenteTo utenteTo) {

        return ResponseEntity.ok(service.saveOrUpdateUtente(utenteTo));
    }

    @PutMapping("/disattiva/{idUtente}")
    public ResponseEntity<UtenteTo> disattivaUtente(
            @PathVariable @Min(1) Integer idUtente) {

        return ResponseEntity.ok(service.disattivaUtente(idUtente));
    }

    @PutMapping("/riattiva/{idUtente}")
    public ResponseEntity<UtenteTo> riattivaUtente(
            @PathVariable @Min(1) Integer idUtente) {

        return ResponseEntity.ok(service.riattivaUtente(idUtente));
    }

    @GetMapping("/cerca-per-id/{idUtente}")
    public ResponseEntity<UtenteTo> cercaUtentePerId(
            @PathVariable @Min(1) Integer idUtente) {

        return ResponseEntity.ok(service.getUtenteById(idUtente));
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<List<UtenteTo>> stampaTutti() {
        return ResponseEntity.ok(service.getAllUtenti());
    }

    @DeleteMapping("/elimina-per-id/{idUtente}")
    public ResponseEntity<EsitoResponse> eliminaUtente(
            @PathVariable @Min(1) Integer idUtente) {

        return ResponseEntity.ok(service.deleteUtente(idUtente));
    }
}