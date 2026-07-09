package com.prenotazioni.controller;

import com.prenotazioni.dto.CollaboratoreServizioTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.CollaboratoreServizioService;
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
        name = "Collaboratori Servizi",
        description = "API per gestione collaboratori servizi"
)
@Validated
@RestController
@RequestMapping("/collaboratore-servizio")
public class CollaboratoreServizioController {

    private static final Logger log = LoggerFactory.getLogger(CollaboratoreServizioController.class);

    private final CollaboratoreServizioService collaboratoreServizioService;

    public CollaboratoreServizioController(CollaboratoreServizioService collaboratoreServizioService) {
        this.collaboratoreServizioService = collaboratoreServizioService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<CollaboratoreServizioTo> inserimentoCollaboratoreServizio(
            @Validated(ValidationGroups.Create.class) @RequestBody CollaboratoreServizioTo collaboratoreServizioTo) {

        log.info("API richiesta inserimento associazione collaboratore-servizio | idCollaboratore={} | idServizio={}",
                collaboratoreServizioTo != null ? collaboratoreServizioTo.getIdCollaboratore() : null,
                collaboratoreServizioTo != null ? collaboratoreServizioTo.getIdServizio() : null);

        return ResponseEntity.ok(
                collaboratoreServizioService.saveOrUpdateCollaboratoreServizio(collaboratoreServizioTo)
        );
    }

    @PutMapping("/modifica")
    public ResponseEntity<CollaboratoreServizioTo> modificaCollaboratoreServizio(
            @Validated(ValidationGroups.Update.class) @RequestBody CollaboratoreServizioTo collaboratoreServizioTo) {

        log.info("API richiesta modifica associazione collaboratore-servizio | idCollaboratoreServizio={} | idCollaboratore={} | idServizio={}",
                collaboratoreServizioTo != null ? collaboratoreServizioTo.getIdCollaboratoreServizio() : null,
                collaboratoreServizioTo != null ? collaboratoreServizioTo.getIdCollaboratore() : null,
                collaboratoreServizioTo != null ? collaboratoreServizioTo.getIdServizio() : null);

        return ResponseEntity.ok(
                collaboratoreServizioService.saveOrUpdateCollaboratoreServizio(collaboratoreServizioTo)
        );
    }

    @GetMapping("/cerca-per-id/{idCollaboratoreServizio}")
    public ResponseEntity<CollaboratoreServizioTo> cercaCollaboratoreServizioPerId(
            @PathVariable @Min(1) Integer idCollaboratoreServizio) {

        log.info("API richiesta ricerca associazione collaboratore-servizio per id | idCollaboratoreServizio={}",
                idCollaboratoreServizio);

        return ResponseEntity.ok(
                collaboratoreServizioService.getCollaboratoreServizioById(idCollaboratoreServizio)
        );
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<Page<CollaboratoreServizioTo>> stampaTuttiCollaboratoriServizi(
            @PageableDefault(
                    size = 20,
                    sort = "idCollaboratoreServizio",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        log.info("API richiesta lista associazioni collaboratore-servizio paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return ResponseEntity.ok(collaboratoreServizioService.getAllCollaboratoriServizi(pageable));
    }

    @GetMapping("/stampa-servizi-per-collaboratore/{idCollaboratore}")
    public ResponseEntity<List<CollaboratoreServizioTo>> stampaServiziPerCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        log.info("API richiesta lista servizi per collaboratore | idCollaboratore={}", idCollaboratore);

        return ResponseEntity.ok(
                collaboratoreServizioService.getServiziByCollaboratore(idCollaboratore)
        );
    }

    @GetMapping("/stampa-collaboratori-per-servizio/{idServizio}")
    public ResponseEntity<List<CollaboratoreServizioTo>> stampaCollaboratoriPerServizio(
            @PathVariable @Min(1) Integer idServizio) {

        log.info("API richiesta lista collaboratori per servizio | idServizio={}", idServizio);

        return ResponseEntity.ok(
                collaboratoreServizioService.getCollaboratoriByServizio(idServizio)
        );
    }

    @DeleteMapping("/elimina-per-id/{idCollaboratoreServizio}")
    public ResponseEntity<EsitoResponse> eliminaCollaboratoreServizio(
            @PathVariable @Min(1) Integer idCollaboratoreServizio) {

        log.info("API richiesta eliminazione associazione collaboratore-servizio | idCollaboratoreServizio={}",
                idCollaboratoreServizio);

        return ResponseEntity.ok(
                collaboratoreServizioService.deleteCollaboratoreServizio(idCollaboratoreServizio)
        );
    }
}