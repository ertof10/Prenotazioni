package com.prenotazioni.controller;

import com.prenotazioni.dto.PrenotazioneTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.PrenotazioneService;
import com.prenotazioni.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

@Tag(
        name = "Prenotazioni",
        description = "API per gestione prenotazioni"
)
@Validated
@RestController
@RequestMapping("/prenotazione")
public class PrenotazioneController {

    private static final Logger log = LoggerFactory.getLogger(PrenotazioneController.class);

    private final PrenotazioneService prenotazioneService;

    public PrenotazioneController(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<PrenotazioneTo> inserimentoPrenotazione(
            @Validated(ValidationGroups.Create.class) @RequestBody PrenotazioneTo prenotazioneTo) {

        log.info("API richiesta inserimento prenotazione | idUtente={} | idServizio={} | idCollaboratore={} | data={}",
                prenotazioneTo != null ? prenotazioneTo.getIdUtente() : null,
                prenotazioneTo != null ? prenotazioneTo.getIdServizio() : null,
                prenotazioneTo != null ? prenotazioneTo.getIdCollaboratore() : null,
                prenotazioneTo != null ? prenotazioneTo.getDataPrenotazione() : null);

        return ResponseEntity.ok(prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<PrenotazioneTo> modificaPrenotazione(
            @Validated(ValidationGroups.Update.class) @RequestBody PrenotazioneTo prenotazioneTo) {

        log.info("API richiesta modifica prenotazione | idPrenotazione={} | idUtente={} | idServizio={} | idCollaboratore={} | data={}",
                prenotazioneTo != null ? prenotazioneTo.getIdPrenotazione() : null,
                prenotazioneTo != null ? prenotazioneTo.getIdUtente() : null,
                prenotazioneTo != null ? prenotazioneTo.getIdServizio() : null,
                prenotazioneTo != null ? prenotazioneTo.getIdCollaboratore() : null,
                prenotazioneTo != null ? prenotazioneTo.getDataPrenotazione() : null);

        return ResponseEntity.ok(prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
    }

    @PutMapping("/utente-annulla-prenotazione/{idPrenotazione}")
    public ResponseEntity<PrenotazioneTo> utenteAnnullaPrenotazione(
            @PathVariable @Min(1) Integer idPrenotazione) {

        log.info("API richiesta annullamento prenotazione da utente | idPrenotazione={}", idPrenotazione);

        return ResponseEntity.ok(prenotazioneService.utenteAnnullaPrenotazione(idPrenotazione));
    }

    @PutMapping("/struttura-annulla-prenotazione/{idPrenotazione}")
    public ResponseEntity<PrenotazioneTo> strutturaAnnullaPrenotazione(
            @PathVariable @Min(1) Integer idPrenotazione) {

        log.info("API richiesta annullamento prenotazione da struttura | idPrenotazione={}", idPrenotazione);

        return ResponseEntity.ok(prenotazioneService.strutturaAnnullaPrenotazione(idPrenotazione));
    }

    @GetMapping("/cerca-per-id/{idPrenotazione}")
    public ResponseEntity<PrenotazioneTo> cercaPrenotazionePerId(
            @PathVariable @Min(1) Integer idPrenotazione) {

        log.info("API richiesta ricerca prenotazione per id | idPrenotazione={}", idPrenotazione);

        return ResponseEntity.ok(prenotazioneService.getPrenotazioneById(idPrenotazione));
    }

    @GetMapping("/stampa-da-riprogrammare")
    public ResponseEntity<List<PrenotazioneTo>> stampaPrenotazioniDaRiprogrammare() {

        log.info("API richiesta lista prenotazioni da riprogrammare");

        return ResponseEntity.ok(prenotazioneService.getPrenotazioniDaRiprogrammare());
    }

    @GetMapping("/stampa-da-riprogrammare-per-utente/{idUtente}")
    public ResponseEntity<List<PrenotazioneTo>> stampaPrenotazioniDaRiprogrammarePerUtente(
            @PathVariable @Min(1) Integer idUtente) {

        log.info("API richiesta lista prenotazioni da riprogrammare per utente | idUtente={}", idUtente);

        return ResponseEntity.ok(prenotazioneService.getPrenotazioniDaRiprogrammareByUtente(idUtente));
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<Page<PrenotazioneTo>> stampaTuttePrenotazioni(
            @PageableDefault(
                    size = 20,
                    sort = "dataPrenotazione",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        log.info("API richiesta lista prenotazioni paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return ResponseEntity.ok(prenotazioneService.getAllPrenotazioni(pageable));
    }

    @GetMapping("/stampa-per-utente/{idUtente}")
    public ResponseEntity<List<PrenotazioneTo>> stampaPrenotazioniPerUtente(
            @PathVariable @Min(1) Integer idUtente) {

        log.info("API richiesta lista prenotazioni per utente | idUtente={}", idUtente);

        return ResponseEntity.ok(prenotazioneService.getPrenotazioniByUtente(idUtente));
    }

    @DeleteMapping("/elimina-per-id/{idPrenotazione}")
    public ResponseEntity<EsitoResponse> eliminaPrenotazione(
            @PathVariable @Min(1) Integer idPrenotazione) {

        log.info("API richiesta eliminazione prenotazione | idPrenotazione={}", idPrenotazione);

        return ResponseEntity.ok(prenotazioneService.deletePrenotazione(idPrenotazione));
    }
}