package com.prenotazioni.controller;

import com.prenotazioni.dto.PrenotazioneTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.PrenotazioneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/prenotazione")
public class PrenotazioneController {

    private final PrenotazioneService prenotazioneService;

    public PrenotazioneController(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<PrenotazioneTo> inserimentoPrenotazione(@Valid @RequestBody PrenotazioneTo prenotazioneTo) {
        return ResponseEntity.ok(prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
    }


    @PutMapping("/modifica")
    public ResponseEntity<PrenotazioneTo> modificaPrenotazione(@Valid @RequestBody PrenotazioneTo prenotazioneTo) {
        return ResponseEntity.ok(prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
    }

    @PutMapping("/utente-annulla-prenotazione/{idPrenotazione}")
    public ResponseEntity<PrenotazioneTo> utenteAnnullaPrenotazione(@PathVariable Integer idPrenotazione) {
        return ResponseEntity.ok(prenotazioneService.utenteAnnullaPrenotazione(idPrenotazione));
    }

    @PutMapping("/struttura-annulla-prenotazione/{idPrenotazione}")
    public ResponseEntity<PrenotazioneTo> strutturaAnnullaPrenotazione(@PathVariable Integer idPrenotazione) {
        return ResponseEntity.ok(prenotazioneService.strutturaAnnullaPrenotazione(idPrenotazione));
    }

    @GetMapping("/cerca-per-id/{idPrenotazione}")
    public ResponseEntity<PrenotazioneTo> cercaPrenotazionePerId(@PathVariable Integer idPrenotazione) {
        return ResponseEntity.ok(prenotazioneService.getPrenotazioneById(idPrenotazione));
    }

    @GetMapping("/stampa-da-riprogrammare")
    public ResponseEntity<List<PrenotazioneTo>> stampaPrenotazioniDaRiprogrammare() {
        return ResponseEntity.ok(
                prenotazioneService.getPrenotazioniDaRiprogrammare()
        );
    }

    @GetMapping("/stampa-da-riprogrammare-per-utente/{idUtente}")
    public ResponseEntity<List<PrenotazioneTo>> stampaPrenotazioniDaRiprogrammarePerUtente(
            @PathVariable Integer idUtente) {

        return ResponseEntity.ok(
                prenotazioneService.getPrenotazioniDaRiprogrammareByUtente(idUtente)
        );
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<List<PrenotazioneTo>> stampaTuttePrenotazioni() {
        return ResponseEntity.ok(prenotazioneService.getAllPrenotazioni());
    }

    @GetMapping("/stampa-per-utente/{idUtente}")
    public ResponseEntity<List<PrenotazioneTo>> stampaPrenotazioniPerUtente(@PathVariable Integer idUtente) {
        return ResponseEntity.ok(prenotazioneService.getPrenotazioniByUtente(idUtente));
    }

    @DeleteMapping("/elimina-per-id/{idPrenotazione}")
    public ResponseEntity<EsitoResponse> eliminaPrenotazione(@PathVariable Integer idPrenotazione) {
        return ResponseEntity.ok(prenotazioneService.deletePrenotazione(idPrenotazione));
    }
}