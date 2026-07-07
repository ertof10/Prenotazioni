package com.prenotazioni.controller;

import com.prenotazioni.dto.ServizioTo;
import com.prenotazioni.response.EsitoResponse;
import com.prenotazioni.service.ServizioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/servizio")
public class ServizioController {

    private final ServizioService servizioService;

    public ServizioController(ServizioService servizioService) {
        this.servizioService = servizioService;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<ServizioTo> inserimentoServizio(@Valid @RequestBody ServizioTo servizioTo) {
        return ResponseEntity.ok(servizioService.saveOrUpdateServizio(servizioTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<ServizioTo> modificaServizio(@Valid @RequestBody ServizioTo servizioTo) {
        return ResponseEntity.ok(servizioService.saveOrUpdateServizio(servizioTo));
    }
    @PutMapping("/disattiva/{idServizio}")
    public ResponseEntity<ServizioTo> disattivaServizio(@PathVariable Integer idServizio) {
        return ResponseEntity.ok(servizioService.disattivaServizio(idServizio));
    }
    @PutMapping("/riattiva/{idServizio}")
    public ResponseEntity<ServizioTo> riattivaServizio(@PathVariable Integer idServizio) {
        return ResponseEntity.ok(servizioService.riattivaServizio(idServizio));
    }

    @GetMapping("/cerca-per-id/{idServizio}")
    public ResponseEntity<ServizioTo> cercaServizioPerId(@PathVariable Integer idServizio) {
        return ResponseEntity.ok(servizioService.getServizioById(idServizio));
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<List<ServizioTo>> stampaTuttiServizi() {
        return ResponseEntity.ok(servizioService.getAllServizi());
    }

    @GetMapping("/stampa-attivi")
    public ResponseEntity<List<ServizioTo>> stampaServiziAttivi() {
        return ResponseEntity.ok(servizioService.getServiziAttivi());
    }

    @DeleteMapping("/elimina-per-id/{idServizio}")
    public ResponseEntity<EsitoResponse> eliminaServizio(@PathVariable Integer idServizio) {
        return ResponseEntity.ok(servizioService.deleteServizio(idServizio));
    }
}