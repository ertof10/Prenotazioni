package com.prenotazioni.controller;

import com.prenotazioni.dto.PreventivoTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.PreventivoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/preventivo")
public class PreventivoController {
    private final PreventivoService preventivoService;

    public PreventivoController(PreventivoService preventivoService) {
        this.preventivoService = preventivoService;
    }

    @PostMapping("/richiesta")
    public ResponseEntity<PreventivoTo> richiediPreventivo(@Valid @RequestBody PreventivoTo preventivoTo) {
        return ResponseEntity.ok(preventivoService.richiediPreventivo(preventivoTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<PreventivoTo> modificaPreventivo(@Valid @RequestBody PreventivoTo preventivoTo) {
        return ResponseEntity.ok(preventivoService.modificaPreventivo(preventivoTo));
    }

    @PutMapping("/prendi-in-lavorazione/{idPreventivo}")
    public ResponseEntity<PreventivoTo> prendiInLavorazionePreventivo(@PathVariable Integer idPreventivo) {
        return ResponseEntity.ok(preventivoService.prendiInLavorazionePreventivo(idPreventivo));
    }

    @PutMapping("/invia/{idPreventivo}")
    public ResponseEntity<PreventivoTo> inviaPreventivo(@PathVariable Integer idPreventivo) {
        return ResponseEntity.ok(preventivoService.inviaPreventivo(idPreventivo));
    }

    @PutMapping("/accetta/{idPreventivo}")
    public ResponseEntity<PreventivoTo> accettaPreventivo(@PathVariable Integer idPreventivo) {
        return ResponseEntity.ok(preventivoService.accettaPreventivo(idPreventivo));
    }

    @PutMapping("/rifiuta/{idPreventivo}")
    public ResponseEntity<PreventivoTo> rifiutaPreventivo(@PathVariable Integer idPreventivo) {
        return ResponseEntity.ok(preventivoService.rifiutaPreventivo(idPreventivo));
    }

    @GetMapping("/cerca-per-id/{idPreventivo}")
    public ResponseEntity<PreventivoTo> cercaPreventivoPerId(@PathVariable Integer idPreventivo) {
        return ResponseEntity.ok(preventivoService.getPreventivoById(idPreventivo));
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<List<PreventivoTo>> stampaTuttiPreventivi() {
        return ResponseEntity.ok(preventivoService.getAllPreventivi());
    }

    @GetMapping("/stampa-per-utente/{idUtente}")
    public ResponseEntity<List<PreventivoTo>> stampaPreventiviPerUtente(@PathVariable Integer idUtente) {
        return ResponseEntity.ok(preventivoService.getPreventiviByUtente(idUtente));
    }

    @GetMapping("/stampa-per-stato/{statoPreventivo}")
    public ResponseEntity<List<PreventivoTo>> stampaPreventiviPerStato(@PathVariable String statoPreventivo) {
        return ResponseEntity.ok(preventivoService.getPreventiviByStato(statoPreventivo));
    }

    @DeleteMapping("/elimina-per-id/{idPreventivo}")
    public ResponseEntity<EsitoResponse> eliminaPreventivo(@PathVariable Integer idPreventivo) {
        return ResponseEntity.ok(preventivoService.deletePreventivo(idPreventivo));
    }

    @PutMapping("/chiudi-manualmente/{idPreventivo}")
    public ResponseEntity<PreventivoTo> chiudiManualmentePreventivo(@PathVariable Integer idPreventivo) {
        return ResponseEntity.ok(preventivoService.chiudiManualmentePreventivo(idPreventivo)
        );
    }
}
