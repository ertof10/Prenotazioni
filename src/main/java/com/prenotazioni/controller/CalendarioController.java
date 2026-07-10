package com.prenotazioni.controller;

import com.prenotazioni.dto.CalendarioTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.CalendarioService;
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
import java.util.List;

@Tag(
        name = "Calendari",
        description = "API per gestione calendari"
)
@Validated
@RestController
@RequestMapping("/calendario")
public class CalendarioController {

    private static final Logger log = LoggerFactory.getLogger(CalendarioController.class);

    private final CalendarioService calendarioService;

    public CalendarioController(CalendarioService calendarioService) {
        this.calendarioService = calendarioService;
    }

    @PreAuthorize("hasRole('ADMIN') or @accessoSecurityService.isCollaboratoreAutenticato(#calendarioTo.idCollaboratore)")
    @PostMapping("/inserimento")
    public ResponseEntity<CalendarioTo> inserimentoCalendario(
            @Validated(ValidationGroups.Create.class) @RequestBody CalendarioTo calendarioTo) {

        log.info("API richiesta inserimento calendario | idCollaboratore={} | giorno={}",
                calendarioTo != null ? calendarioTo.getIdCollaboratore() : null,
                calendarioTo != null ? calendarioTo.getGiornoSettimanaCalendario() : null);

        return ResponseEntity.ok(calendarioService.saveOrUpdateCalendario(calendarioTo));
    }

    @PreAuthorize("hasRole('ADMIN') or @accessoSecurityService.isCollaboratoreAutenticato(#calendarioTo.idCollaboratore)")
    @PutMapping("/modifica")
    public ResponseEntity<CalendarioTo> modificaCalendario(
            @Validated(ValidationGroups.Update.class) @RequestBody CalendarioTo calendarioTo) {

        log.info("API richiesta modifica calendario | idCalendario={} | idCollaboratore={} | giorno={}",
                calendarioTo != null ? calendarioTo.getIdCalendario() : null,
                calendarioTo != null ? calendarioTo.getIdCollaboratore() : null,
                calendarioTo != null ? calendarioTo.getGiornoSettimanaCalendario() : null);

        return ResponseEntity.ok(calendarioService.saveOrUpdateCalendario(calendarioTo));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cerca-per-id/{idCalendario}")
    public ResponseEntity<CalendarioTo> cercaCalendarioPerId(
            @PathVariable @Min(1) Integer idCalendario) {

        log.info("API richiesta ricerca calendario per id | idCalendario={}", idCalendario);

        return ResponseEntity.ok(calendarioService.getCalendarioById(idCalendario));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stampa-tutti")
    public ResponseEntity<Page<CalendarioTo>> stampaTuttiCalendari(
            @PageableDefault(
                    size = 20,
                    sort = "idCalendario",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        log.info("API richiesta lista calendari paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return ResponseEntity.ok(calendarioService.getAllCalendari(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stampa-attivi")
    public ResponseEntity<List<CalendarioTo>> stampaCalendariAttivi() {

        log.info("API richiesta lista calendari attivi");

        return ResponseEntity.ok(calendarioService.getCalendariAttivi());
    }

    @PreAuthorize("hasRole('ADMIN') or @accessoSecurityService.isCollaboratoreAutenticato(#idCollaboratore)")
    @GetMapping("/stampa-per-collaboratore/{idCollaboratore}")
    public ResponseEntity<List<CalendarioTo>> stampaCalendariPerCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        log.info("API richiesta lista calendari per collaboratore | idCollaboratore={}", idCollaboratore);

        return ResponseEntity.ok(calendarioService.getCalendariByCollaboratore(idCollaboratore));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'UTENTE') or @accessoSecurityService.isCollaboratoreAutenticato(#idCollaboratore)")
    @GetMapping("/stampa-attivi-per-collaboratore/{idCollaboratore}")
    public ResponseEntity<List<CalendarioTo>> stampaCalendariAttiviPerCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        log.info("API richiesta lista calendari attivi per collaboratore | idCollaboratore={}", idCollaboratore);

        return ResponseEntity.ok(calendarioService.getCalendariAttiviByCollaboratore(idCollaboratore));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/elimina-per-id/{idCalendario}")
    public ResponseEntity<EsitoResponse> eliminaCalendario(
            @PathVariable @Min(1) Integer idCalendario) {

        log.info("API richiesta eliminazione calendario | idCalendario={}", idCalendario);

        return ResponseEntity.ok(calendarioService.deleteCalendario(idCalendario));
    }
}