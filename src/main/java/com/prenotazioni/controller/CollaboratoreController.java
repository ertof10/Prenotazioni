package com.prenotazioni.controller;

import com.prenotazioni.dto.CollaboratoreTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.CollaboratoreService;
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
        name = "Collaboratori",
        description = "API per gestione collaboratori"
)
@Validated
@RestController
@RequestMapping("/collaboratore")
public class CollaboratoreController {

    private static final Logger log = LoggerFactory.getLogger(CollaboratoreController.class);

    private final CollaboratoreService collaboratoreService;

    public CollaboratoreController(CollaboratoreService collaboratoreService) {
        this.collaboratoreService = collaboratoreService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/inserimento")
    public ResponseEntity<CollaboratoreTo> inserimentoCollaboratore(
            @Validated(ValidationGroups.Create.class) @RequestBody CollaboratoreTo collaboratoreTo) {

        log.info("API richiesta inserimento collaboratore | email={}",
                collaboratoreTo != null ? collaboratoreTo.getEmailCollaboratore() : null);

        return ResponseEntity.ok(collaboratoreService.saveOrUpdateCollaboratore(collaboratoreTo));
    }

    @PreAuthorize("hasRole('ADMIN') or @accessoSecurityService.isCollaboratoreAutenticato(#collaboratoreTo.idCollaboratore)")
    @PutMapping("/modifica")
    public ResponseEntity<CollaboratoreTo> modificaCollaboratore(
            @Validated(ValidationGroups.Update.class) @RequestBody CollaboratoreTo collaboratoreTo) {

        log.info("API richiesta modifica collaboratore | idCollaboratore={} | email={}",
                collaboratoreTo != null ? collaboratoreTo.getIdCollaboratore() : null,
                collaboratoreTo != null ? collaboratoreTo.getEmailCollaboratore() : null);

        return ResponseEntity.ok(collaboratoreService.saveOrUpdateCollaboratore(collaboratoreTo));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/disattiva/{idCollaboratore}")
    public ResponseEntity<CollaboratoreTo> disattivaCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        log.info("API richiesta disattivazione collaboratore | idCollaboratore={}", idCollaboratore);

        return ResponseEntity.ok(collaboratoreService.disattivaCollaboratore(idCollaboratore));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/riattiva/{idCollaboratore}")
    public ResponseEntity<CollaboratoreTo> riattivaCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        log.info("API richiesta riattivazione collaboratore | idCollaboratore={}", idCollaboratore);

        return ResponseEntity.ok(collaboratoreService.riattivaCollaboratore(idCollaboratore));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('UTENTE') or @accessoSecurityService.isCollaboratoreAutenticato(#idCollaboratore)")
    @GetMapping("/cerca-per-id/{idCollaboratore}")
    public ResponseEntity<CollaboratoreTo> cercaCollaboratorePerId(
            @PathVariable @Min(1) Integer idCollaboratore) {

        log.info("API richiesta ricerca collaboratore per id | idCollaboratore={}", idCollaboratore);

        return ResponseEntity.ok(collaboratoreService.getCollaboratoreById(idCollaboratore));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stampa-tutti")
    public ResponseEntity<Page<CollaboratoreTo>> stampaTuttiCollaboratori(
            @PageableDefault(
                    size = 20,
                    sort = "idCollaboratore",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        log.info("API richiesta lista collaboratori paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return ResponseEntity.ok(collaboratoreService.getAllCollaboratori(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'UTENTE')")
    @GetMapping("/stampa-attivi")
    public ResponseEntity<List<CollaboratoreTo>> stampaCollaboratoriAttivi() {

        log.info("API richiesta lista collaboratori attivi");

        return ResponseEntity.ok(collaboratoreService.getCollaboratoriAttivi());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/elimina-per-id/{idCollaboratore}")
    public ResponseEntity<EsitoResponse> eliminaCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        log.info("API richiesta eliminazione collaboratore | idCollaboratore={}", idCollaboratore);

        return ResponseEntity.ok(collaboratoreService.deleteCollaboratore(idCollaboratore));
    }
}