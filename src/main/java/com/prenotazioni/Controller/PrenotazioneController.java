package com.prenotazioni.Controller;

import com.prenotazioni.Dto.PrenotazioneTo;
import com.prenotazioni.Response.EsitoResponse;
import com.prenotazioni.Service.PrenotazioneService;
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

    @PutMapping("/annulla/{idPrenotazione}")
    public ResponseEntity<PrenotazioneTo> annullaPrenotazione(@PathVariable Integer idPrenotazione) {
        return ResponseEntity.ok(prenotazioneService.annullaPrenotazione(idPrenotazione));
    }

    @GetMapping("/cerca-per-id/{idPrenotazione}")
    public ResponseEntity<PrenotazioneTo> cercaPrenotazionePerId(@PathVariable Integer idPrenotazione) {
        return ResponseEntity.ok(prenotazioneService.getPrenotazioneById(idPrenotazione));
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