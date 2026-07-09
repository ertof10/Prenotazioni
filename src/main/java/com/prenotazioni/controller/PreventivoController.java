package com.prenotazioni.controller;

import com.prenotazioni.dto.PreventivoTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.PreventivoService;
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
import javax.validation.constraints.NotBlank;

@Tag(
        name = "Preventivi",
        description = "API per gestione preventivi"
)
@Validated
@RestController
@RequestMapping("/preventivo")
public class PreventivoController {

    private static final Logger log = LoggerFactory.getLogger(PreventivoController.class);

    private final PreventivoService preventivoService;

    public PreventivoController(PreventivoService preventivoService) {
        this.preventivoService = preventivoService;
    }

    @PostMapping("/richiesta")
    public ResponseEntity<PreventivoTo> richiediPreventivo(
            @Validated(ValidationGroups.Create.class) @RequestBody PreventivoTo preventivoTo) {

        log.info("API richiesta creazione preventivo | idUtente={} | idServizio={}",
                preventivoTo != null ? preventivoTo.getIdUtente() : null,
                preventivoTo != null ? preventivoTo.getIdServizio() : null);

        return ResponseEntity.ok(preventivoService.richiediPreventivo(preventivoTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<PreventivoTo> modificaPreventivo(
            @Validated(ValidationGroups.Update.class) @RequestBody PreventivoTo preventivoTo) {

        log.info("API richiesta modifica preventivo | idPreventivo={} | idUtente={} | idServizio={}",
                preventivoTo != null ? preventivoTo.getIdPreventivo() : null,
                preventivoTo != null ? preventivoTo.getIdUtente() : null,
                preventivoTo != null ? preventivoTo.getIdServizio() : null);

        return ResponseEntity.ok(preventivoService.modificaPreventivo(preventivoTo));
    }

    @PutMapping("/prendi-in-lavorazione/{idPreventivo}")
    public ResponseEntity<PreventivoTo> prendiInLavorazionePreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        log.info("API richiesta presa in lavorazione preventivo | idPreventivo={}", idPreventivo);

        return ResponseEntity.ok(preventivoService.prendiInLavorazionePreventivo(idPreventivo));
    }

    @PutMapping("/invia/{idPreventivo}")
    public ResponseEntity<PreventivoTo> inviaPreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        log.info("API richiesta invio preventivo | idPreventivo={}", idPreventivo);

        return ResponseEntity.ok(preventivoService.inviaPreventivo(idPreventivo));
    }

    @PutMapping("/accetta/{idPreventivo}")
    public ResponseEntity<PreventivoTo> accettaPreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        log.info("API richiesta accettazione preventivo | idPreventivo={}", idPreventivo);

        return ResponseEntity.ok(preventivoService.accettaPreventivo(idPreventivo));
    }

    @PutMapping("/rifiuta/{idPreventivo}")
    public ResponseEntity<PreventivoTo> rifiutaPreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        log.info("API richiesta rifiuto preventivo | idPreventivo={}", idPreventivo);

        return ResponseEntity.ok(preventivoService.rifiutaPreventivo(idPreventivo));
    }

    @GetMapping("/cerca-per-id/{idPreventivo}")
    public ResponseEntity<PreventivoTo> cercaPreventivoPerId(
            @PathVariable @Min(1) Integer idPreventivo) {

        log.info("API richiesta ricerca preventivo per id | idPreventivo={}", idPreventivo);

        return ResponseEntity.ok(preventivoService.getPreventivoById(idPreventivo));
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<Page<PreventivoTo>> stampaTuttiPreventivi(
            @PageableDefault(
                    size = 20,
                    sort = "idPreventivo",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        log.info("API richiesta lista preventivi paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return ResponseEntity.ok(preventivoService.getAllPreventivi(pageable));
    }

    @GetMapping("/stampa-per-utente/{idUtente}")
    public ResponseEntity<Page<PreventivoTo>> stampaPreventiviPerUtente(
            @PathVariable @Min(1) Integer idUtente,
            @PageableDefault(
                    size = 20,
                    sort = "idPreventivo",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        log.info("API richiesta lista preventivi per utente | idUtente={} | page={} | size={} | sort={}",
                idUtente,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return ResponseEntity.ok(preventivoService.getPreventiviByUtente(idUtente, pageable));
    }

    @GetMapping("/stampa-per-stato/{statoPreventivo}")
    public ResponseEntity<Page<PreventivoTo>> stampaPreventiviPerStato(
            @PathVariable @NotBlank String statoPreventivo,
            @PageableDefault(
                    size = 20,
                    sort = "idPreventivo",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        log.info("API richiesta lista preventivi per stato | statoPreventivo={} | page={} | size={} | sort={}",
                statoPreventivo,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return ResponseEntity.ok(preventivoService.getPreventiviByStato(statoPreventivo, pageable));
    }

    @DeleteMapping("/elimina-per-id/{idPreventivo}")
    public ResponseEntity<EsitoResponse> eliminaPreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        log.info("API richiesta eliminazione preventivo | idPreventivo={}", idPreventivo);

        return ResponseEntity.ok(preventivoService.deletePreventivo(idPreventivo));
    }

    @PutMapping("/chiudi-manualmente/{idPreventivo}")
    public ResponseEntity<PreventivoTo> chiudiManualmentePreventivo(
            @PathVariable @Min(1) Integer idPreventivo) {

        log.info("API richiesta chiusura manuale preventivo | idPreventivo={}", idPreventivo);

        return ResponseEntity.ok(preventivoService.chiudiManualmentePreventivo(idPreventivo));
    }
}