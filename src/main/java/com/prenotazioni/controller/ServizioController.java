package com.prenotazioni.controller;

import com.prenotazioni.dto.ServizioTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.ServizioService;
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
        name = "Servizi",
        description = "API per gestione servizi"
)
@Validated
@RestController
@RequestMapping("/servizio")
public class ServizioController {

    private static final Logger log = LoggerFactory.getLogger(ServizioController.class);

    private final ServizioService servizioService;

    public ServizioController(ServizioService servizioService) {
        this.servizioService = servizioService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/inserimento")
    public ResponseEntity<ServizioTo> inserimentoServizio(
            @Validated(ValidationGroups.Create.class) @RequestBody ServizioTo servizioTo) {

        log.info("API richiesta inserimento servizio | nomeServizio={} | tipoGestioneServizio={}",
                servizioTo != null ? servizioTo.getNomeServizio() : null,
                servizioTo != null ? servizioTo.getTipoGestioneServizio() : null);

        return ResponseEntity.ok(servizioService.saveOrUpdateServizio(servizioTo));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/modifica")
    public ResponseEntity<ServizioTo> modificaServizio(
            @Validated(ValidationGroups.Update.class) @RequestBody ServizioTo servizioTo) {

        log.info("API richiesta modifica servizio | idServizio={} | nomeServizio={} | tipoGestioneServizio={}",
                servizioTo != null ? servizioTo.getIdServizio() : null,
                servizioTo != null ? servizioTo.getNomeServizio() : null,
                servizioTo != null ? servizioTo.getTipoGestioneServizio() : null);

        return ResponseEntity.ok(servizioService.saveOrUpdateServizio(servizioTo));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/disattiva/{idServizio}")
    public ResponseEntity<ServizioTo> disattivaServizio(
            @PathVariable @Min(1) Integer idServizio) {

        log.info("API richiesta disattivazione servizio | idServizio={}", idServizio);

        return ResponseEntity.ok(servizioService.disattivaServizio(idServizio));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/riattiva/{idServizio}")
    public ResponseEntity<ServizioTo> riattivaServizio(
            @PathVariable @Min(1) Integer idServizio) {

        log.info("API richiesta riattivazione servizio | idServizio={}", idServizio);

        return ResponseEntity.ok(servizioService.riattivaServizio(idServizio));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'UTENTE', 'COLLABORATORE')")
    @GetMapping("/cerca-per-id/{idServizio}")
    public ResponseEntity<ServizioTo> cercaServizioPerId(
            @PathVariable @Min(1) Integer idServizio) {

        log.info("API richiesta ricerca servizio per id | idServizio={}", idServizio);

        return ResponseEntity.ok(servizioService.getServizioById(idServizio));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stampa-tutti")
    public ResponseEntity<Page<ServizioTo>> stampaTuttiServizi(
            @PageableDefault(
                    size = 20,
                    sort = "idServizio",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        log.info("API richiesta lista servizi paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return ResponseEntity.ok(servizioService.getAllServizi(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'UTENTE', 'COLLABORATORE')")
    @GetMapping("/stampa-attivi")
    public ResponseEntity<Page<ServizioTo>> stampaServiziAttivi(
            @PageableDefault(
                    size = 20,
                    sort = "idServizio",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        log.info("API richiesta lista servizi attivi paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return ResponseEntity.ok(servizioService.getServiziAttivi(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/elimina-per-id/{idServizio}")
    public ResponseEntity<EsitoResponse> eliminaServizio(
            @PathVariable @Min(1) Integer idServizio) {

        log.info("API richiesta eliminazione servizio | idServizio={}", idServizio);

        return ResponseEntity.ok(servizioService.deleteServizio(idServizio));
    }
}