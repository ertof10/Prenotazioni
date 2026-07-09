package com.prenotazioni.controller;

import com.prenotazioni.dto.AssenzaCollaboratoreTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.AssenzaCollaboratoreService;
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
        name = "Assenze Collaboratori",
        description = "API per gestione assenze collaboratori"
)
@Validated
@RestController
@RequestMapping("/assenze-collaboratori")
public class AssenzaCollaboratoreController {

    private static final Logger log = LoggerFactory.getLogger(AssenzaCollaboratoreController.class);

    private final AssenzaCollaboratoreService assenzaCollaboratoreService;

    public AssenzaCollaboratoreController(AssenzaCollaboratoreService assenzaCollaboratoreService) {
        this.assenzaCollaboratoreService = assenzaCollaboratoreService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<AssenzaCollaboratoreTo> inserimentoAssenzaCollaboratore(
            @Validated(ValidationGroups.Create.class) @RequestBody AssenzaCollaboratoreTo assenzaCollaboratoreTo) {

        log.info("API richiesta inserimento assenza collaboratore | idCollaboratore={} | dataInizio={} | dataFine={}",
                assenzaCollaboratoreTo != null ? assenzaCollaboratoreTo.getIdCollaboratore() : null,
                assenzaCollaboratoreTo != null ? assenzaCollaboratoreTo.getDataInizioAssenza() : null,
                assenzaCollaboratoreTo != null ? assenzaCollaboratoreTo.getDataFineAssenza() : null);

        return ResponseEntity.ok(
                assenzaCollaboratoreService.saveOrUpdateAssenzaCollaboratore(assenzaCollaboratoreTo)
        );
    }

    @PutMapping("/modifica")
    public ResponseEntity<AssenzaCollaboratoreTo> modificaAssenzaCollaboratore(
            @Validated(ValidationGroups.Update.class) @RequestBody AssenzaCollaboratoreTo assenzaCollaboratoreTo) {

        log.info("API richiesta modifica assenza collaboratore | idAssenzaCollaboratore={} | idCollaboratore={} | dataInizio={} | dataFine={}",
                assenzaCollaboratoreTo != null ? assenzaCollaboratoreTo.getIdAssenzaCollaboratore() : null,
                assenzaCollaboratoreTo != null ? assenzaCollaboratoreTo.getIdCollaboratore() : null,
                assenzaCollaboratoreTo != null ? assenzaCollaboratoreTo.getDataInizioAssenza() : null,
                assenzaCollaboratoreTo != null ? assenzaCollaboratoreTo.getDataFineAssenza() : null);

        return ResponseEntity.ok(
                assenzaCollaboratoreService.saveOrUpdateAssenzaCollaboratore(assenzaCollaboratoreTo)
        );
    }

    @GetMapping("/cerca-per-id/{idAssenzaCollaboratore}")
    public ResponseEntity<AssenzaCollaboratoreTo> getAssenzaCollaboratoreById(
            @PathVariable @Min(1) Integer idAssenzaCollaboratore) {

        log.info("API richiesta ricerca assenza collaboratore per id | idAssenzaCollaboratore={}",
                idAssenzaCollaboratore);

        return ResponseEntity.ok(
                assenzaCollaboratoreService.getAssenzaCollaboratoreById(idAssenzaCollaboratore)
        );
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<Page<AssenzaCollaboratoreTo>> getAllAssenzeCollaboratori(
            @PageableDefault(
                    size = 20,
                    sort = "idAssenzaCollaboratore",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        log.info("API richiesta lista assenze collaboratori paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return ResponseEntity.ok(
                assenzaCollaboratoreService.getAllAssenzeCollaboratori(pageable)
        );
    }

    @GetMapping("/stampa-per-collaboratore/{idCollaboratore}")
    public ResponseEntity<List<AssenzaCollaboratoreTo>> getAssenzeByCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        log.info("API richiesta lista assenze per collaboratore | idCollaboratore={}", idCollaboratore);

        return ResponseEntity.ok(
                assenzaCollaboratoreService.getAssenzeByCollaboratore(idCollaboratore)
        );
    }

    @DeleteMapping("/elimina/{idAssenzaCollaboratore}")
    public ResponseEntity<EsitoResponse> deleteAssenzaCollaboratore(
            @PathVariable @Min(1) Integer idAssenzaCollaboratore) {

        log.info("API richiesta eliminazione assenza collaboratore | idAssenzaCollaboratore={}",
                idAssenzaCollaboratore);

        return ResponseEntity.ok(
                assenzaCollaboratoreService.deleteAssenzaCollaboratore(idAssenzaCollaboratore)
        );
    }
}