package com.prenotazioni.controller;

import com.prenotazioni.dto.PreventivoTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.PreventivoService;
import com.prenotazioni.validation.ValidationGroups;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@RestController
@RequestMapping("/preventivo")
public class PreventivoController {

    private final PreventivoService preventivoService;

    public PreventivoController(PreventivoService preventivoService) {
        this.preventivoService = preventivoService;
    }

    @PostMapping("/richiesta")
    public ResponseEntity<PreventivoTo> richiediPreventivo(
            @Validated(ValidationGroups.Create.class) @RequestBody PreventivoTo preventivoTo) {

        return ResponseEntity.ok(preventivoService.richiediPreventivo(preventivoTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<PreventivoTo> modificaPreventivo(
            @Validated(ValidationGroups.Update.class) @RequestBody PreventivoTo preventivoTo) {

        return ResponseEntity.ok(preventivoService.modificaPreventivo(preventivoTo));
    }

    @PutMapping("/prendi-in-lavorazione/{idPreventivo}")
    public ResponseEntity<PreventivoTo> prendiInLavorazionePreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        return ResponseEntity.ok(preventivoService.prendiInLavorazionePreventivo(idPreventivo));
    }

    @PutMapping("/invia/{idPreventivo}")
    public ResponseEntity<PreventivoTo> inviaPreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        return ResponseEntity.ok(preventivoService.inviaPreventivo(idPreventivo));
    }

    @PutMapping("/accetta/{idPreventivo}")
    public ResponseEntity<PreventivoTo> accettaPreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        return ResponseEntity.ok(preventivoService.accettaPreventivo(idPreventivo));
    }

    @PutMapping("/rifiuta/{idPreventivo}")
    public ResponseEntity<PreventivoTo> rifiutaPreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        return ResponseEntity.ok(preventivoService.rifiutaPreventivo(idPreventivo));
    }

    @GetMapping("/cerca-per-id/{idPreventivo}")
    public ResponseEntity<PreventivoTo> cercaPreventivoPerId(
            @PathVariable @Min(1) Integer idPreventivo) {

        return ResponseEntity.ok(preventivoService.getPreventivoById(idPreventivo));
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<List<PreventivoTo>> stampaTuttiPreventivi() {
        return ResponseEntity.ok(preventivoService.getAllPreventivi());
    }

    @GetMapping("/stampa-per-utente/{idUtente}")
    public ResponseEntity<List<PreventivoTo>> stampaPreventiviPerUtente(
            @PathVariable @Min(1) Integer idUtente) {

        return ResponseEntity.ok(preventivoService.getPreventiviByUtente(idUtente));
    }

    @GetMapping("/stampa-per-stato/{statoPreventivo}")
    public ResponseEntity<List<PreventivoTo>> stampaPreventiviPerStato(
            @PathVariable @NotBlank String statoPreventivo) {

        return ResponseEntity.ok(preventivoService.getPreventiviByStato(statoPreventivo));
    }

    @DeleteMapping("/elimina-per-id/{idPreventivo}")
    public ResponseEntity<EsitoResponse> eliminaPreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        return ResponseEntity.ok(preventivoService.deletePreventivo(idPreventivo));
    }

    @PutMapping("/chiudi-manualmente/{idPreventivo}")
    public ResponseEntity<PreventivoTo> chiudiManualmentePreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        return ResponseEntity.ok(preventivoService.chiudiManualmentePreventivo(idPreventivo));
    }
}