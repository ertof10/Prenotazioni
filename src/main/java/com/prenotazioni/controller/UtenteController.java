package com.prenotazioni.controller;

import com.prenotazioni.dto.UtenteTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.UtenteService;
import com.prenotazioni.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

@Tag(
        name = "Utenti",
        description = "API per gestione utenti"
)
@Validated
@RestController
@RequestMapping("/utente")
@PreAuthorize("hasRole('ADMIN')")
public class UtenteController {

    private static final Logger log = LoggerFactory.getLogger(UtenteController.class);

    private final UtenteService service;

    public UtenteController(UtenteService service) {
        this.service = service;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<UtenteTo> inserimentoUtente(
            @Validated(ValidationGroups.Create.class) @RequestBody UtenteTo utenteTo) {

        log.info("API richiesta inserimento utente | email={}",
                utenteTo != null ? utenteTo.getEmailUtente() : null);

        return ResponseEntity.ok(service.saveOrUpdateUtente(utenteTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<UtenteTo> modificaUtente(
            @Validated(ValidationGroups.Update.class) @RequestBody UtenteTo utenteTo) {

        log.info("API richiesta modifica utente | idUtente={} | email={}",
                utenteTo != null ? utenteTo.getIdUtente() : null,
                utenteTo != null ? utenteTo.getEmailUtente() : null);

        return ResponseEntity.ok(service.saveOrUpdateUtente(utenteTo));
    }

    @PutMapping("/disattiva/{idUtente}")
    public ResponseEntity<UtenteTo> disattivaUtente(
            @PathVariable @Min(1) Integer idUtente) {

        log.info("API richiesta disattivazione utente | idUtente={}", idUtente);

        return ResponseEntity.ok(service.disattivaUtente(idUtente));
    }

    @PutMapping("/riattiva/{idUtente}")
    public ResponseEntity<UtenteTo> riattivaUtente(
            @PathVariable @Min(1) Integer idUtente) {

        log.info("API richiesta riattivazione utente | idUtente={}", idUtente);

        return ResponseEntity.ok(service.riattivaUtente(idUtente));
    }

    @GetMapping("/cerca-per-id/{idUtente}")
    public ResponseEntity<UtenteTo> cercaUtentePerId(
            @PathVariable @Min(1) Integer idUtente) {

        log.info("API richiesta ricerca utente per id | idUtente={}", idUtente);

        return ResponseEntity.ok(service.getUtenteById(idUtente));
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<Page<UtenteTo>> stampaTutti(
            @PageableDefault(
                    size = 20,
                    sort = "idUtente",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        log.info("API richiesta lista utenti paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return ResponseEntity.ok(service.getAllUtenti(pageable));
    }

    @DeleteMapping("/elimina-per-id/{idUtente}")
    public ResponseEntity<EsitoResponse> eliminaUtente(
            @PathVariable @Min(1) Integer idUtente) {

        log.info("API richiesta eliminazione utente | idUtente={}", idUtente);

        return ResponseEntity.ok(service.deleteUtente(idUtente));
    }
}