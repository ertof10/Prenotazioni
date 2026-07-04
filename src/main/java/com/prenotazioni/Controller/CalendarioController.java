package com.prenotazioni.Controller;

import com.prenotazioni.Dto.CalendarioTo;
import com.prenotazioni.Response.EsitoResponse;
import com.prenotazioni.Service.CalendarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/calendario")
public class CalendarioController {

    private final CalendarioService calendarioService;

    public CalendarioController(CalendarioService calendarioService) {
        this.calendarioService = calendarioService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<CalendarioTo> inserimentoCalendario(@Valid @RequestBody CalendarioTo calendarioTo) {
        return ResponseEntity.ok(calendarioService.saveOrUpdateCalendario(calendarioTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<CalendarioTo> modificaCalendario(@Valid @RequestBody CalendarioTo calendarioTo) {
        return ResponseEntity.ok(calendarioService.saveOrUpdateCalendario(calendarioTo));
    }

    @GetMapping("/cerca-per-id/{idCalendario}")
    public ResponseEntity<CalendarioTo> cercaCalendarioPerId(@PathVariable("idCalendario") Integer idCalendario) {
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
            @PathVariable("idCollaboratore") Integer idCollaboratore) {
        return ResponseEntity.ok(calendarioService.getCalendariByCollaboratore(idCollaboratore));
    }

    @GetMapping("/stampa-attivi-per-collaboratore/{idCollaboratore}")
    public ResponseEntity<List<CalendarioTo>> stampaCalendariAttiviPerCollaboratore(
            @PathVariable("idCollaboratore") Integer idCollaboratore) {
        return ResponseEntity.ok(calendarioService.getCalendariAttiviByCollaboratore(idCollaboratore));
    }

    @DeleteMapping("/elimina-per-id/{idCalendario}")
    public ResponseEntity<EsitoResponse> eliminaCalendario(@PathVariable("idCalendario") Integer idCalendario) {
        return ResponseEntity.ok(calendarioService.deleteCalendario(idCalendario));
    }
}