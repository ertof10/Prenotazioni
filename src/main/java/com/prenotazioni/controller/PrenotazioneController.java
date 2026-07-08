package com.prenotazioni.controller;

import com.prenotazioni.dto.PrenotazioneTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.PrenotazioneService;
import com.prenotazioni.validation.ValidationGroups;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping("/prenotazione")
public class PrenotazioneController {

    private final PrenotazioneService prenotazioneService;

    public PrenotazioneController(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<PrenotazioneTo> inserimentoPrenotazione(
            @Validated(ValidationGroups.Create.class) @RequestBody PrenotazioneTo prenotazioneTo) {

        return ResponseEntity.ok(prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<PrenotazioneTo> modificaPrenotazione(
            @Validated(ValidationGroups.Update.class) @RequestBody PrenotazioneTo prenotazioneTo) {

        return ResponseEntity.ok(prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
    }

    @PutMapping("/utente-annulla-prenotazione/{idPrenotazione}")
    public ResponseEntity<PrenotazioneTo> utenteAnnullaPrenotazione(
            @PathVariable @Min(1) Integer idPrenotazione) {

        return ResponseEntity.ok(prenotazioneService.utenteAnnullaPrenotazione(idPrenotazione));
    }

    @PutMapping("/struttura-annulla-prenotazione/{idPrenotazione}")
    public ResponseEntity<PrenotazioneTo> strutturaAnnullaPrenotazione(
            @PathVariable @Min(1) Integer idPrenotazione) {

        return ResponseEntity.ok(prenotazioneService.strutturaAnnullaPrenotazione(idPrenotazione));
    }

    @GetMapping("/cerca-per-id/{idPrenotazione}")
    public ResponseEntity<PrenotazioneTo> cercaPrenotazionePerId(
            @PathVariable @Min(1) Integer idPrenotazione) {

        return ResponseEntity.ok(prenotazioneService.getPrenotazioneById(idPrenotazione));
    }

    @GetMapping("/stampa-da-riprogrammare")
    public ResponseEntity<List<PrenotazioneTo>> stampaPrenotazioniDaRiprogrammare() {
        return ResponseEntity.ok(prenotazioneService.getPrenotazioniDaRiprogrammare());
    }

    @GetMapping("/stampa-da-riprogrammare-per-utente/{idUtente}")
    public ResponseEntity<List<PrenotazioneTo>> stampaPrenotazioniDaRiprogrammarePerUtente(
            @PathVariable @Min(1) Integer idUtente) {

        return ResponseEntity.ok(prenotazioneService.getPrenotazioniDaRiprogrammareByUtente(idUtente));
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<List<PrenotazioneTo>> stampaTuttePrenotazioni() {
        return ResponseEntity.ok(prenotazioneService.getAllPrenotazioni());
    }

    @GetMapping("/stampa-per-utente/{idUtente}")
    public ResponseEntity<List<PrenotazioneTo>> stampaPrenotazioniPerUtente(
            @PathVariable @Min(1) Integer idUtente) {

        return ResponseEntity.ok(prenotazioneService.getPrenotazioniByUtente(idUtente));
    }

    @DeleteMapping("/elimina-per-id/{idPrenotazione}")
    public ResponseEntity<EsitoResponse> eliminaPrenotazione(
            @PathVariable @Min(1) Integer idPrenotazione) {

        return ResponseEntity.ok(prenotazioneService.deletePrenotazione(idPrenotazione));
    }
}