package com.prenotazioni.controller;

import com.prenotazioni.dto.CalendarioTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.CalendarioService;
import com.prenotazioni.validation.ValidationGroups;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping("/calendario")
public class CalendarioController {

    private final CalendarioService calendarioService;

    public CalendarioController(CalendarioService calendarioService) {
        this.calendarioService = calendarioService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<CalendarioTo> inserimentoCalendario(
            @Validated(ValidationGroups.Create.class) @RequestBody CalendarioTo calendarioTo) {

        return ResponseEntity.ok(calendarioService.saveOrUpdateCalendario(calendarioTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<CalendarioTo> modificaCalendario(
            @Validated(ValidationGroups.Update.class) @RequestBody CalendarioTo calendarioTo) {

        return ResponseEntity.ok(calendarioService.saveOrUpdateCalendario(calendarioTo));
    }

    @GetMapping("/cerca-per-id/{idCalendario}")
    public ResponseEntity<CalendarioTo> cercaCalendarioPerId(
            @PathVariable @Min(1) Integer idCalendario) {

        return ResponseEntity.ok(calendarioService.getCalendarioById(idCalendario));
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<List<CalendarioTo>> stampaTuttiCalendari() {
        return ResponseEntity.ok(calendarioService.getAllCalendari());
    }

    @GetMapping("/stampa-attivi")
    public ResponseEntity<List<CalendarioTo>> stampaCalendariAttivi() {
        return ResponseEntity.ok(calendarioService.getCalendariAttivi());
    }

    @GetMapping("/stampa-per-collaboratore/{idCollaboratore}")
    public ResponseEntity<List<CalendarioTo>> stampaCalendariPerCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        return ResponseEntity.ok(calendarioService.getCalendariByCollaboratore(idCollaboratore));
    }

    @GetMapping("/stampa-attivi-per-collaboratore/{idCollaboratore}")
    public ResponseEntity<List<CalendarioTo>> stampaCalendariAttiviPerCollaboratore(
            @PathVariable @Min(1) Integer idCollaboratore) {

        return ResponseEntity.ok(calendarioService.getCalendariAttiviByCollaboratore(idCollaboratore));
    }

    @DeleteMapping("/elimina-per-id/{idCalendario}")
    public ResponseEntity<EsitoResponse> eliminaCalendario(
            @PathVariable @Min(1) Integer idCalendario) {

        return ResponseEntity.ok(calendarioService.deleteCalendario(idCalendario));
    }
}