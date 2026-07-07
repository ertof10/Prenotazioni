package com.prenotazioni.Controller;




import com.prenotazioni.Dto.UtenteTo;
import com.prenotazioni.Response.EsitoResponse;
import com.prenotazioni.Service.UtenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    private final UtenteService service;

    public UtenteController(UtenteService service) {

        this.service = service;
    }

    @PostMapping("/inserimento")
    public ResponseEntity<UtenteTo> inserimentoUtente(@Valid @RequestBody UtenteTo utenteTo) {
        utenteTo.setIdUtente(null);
        return ResponseEntity.ok(service.saveOrUpdateUtente(utenteTo));
    }

    @PutMapping("/modifica")
    public ResponseEntity<UtenteTo> modificaUtente(@Valid @RequestBody UtenteTo utenteTo) {
        return ResponseEntity.ok(service.saveOrUpdateUtente(utenteTo));
    }
    @PutMapping("/disattiva/{idUtente}")
    public ResponseEntity<UtenteTo> disattivaUtente(@PathVariable Integer idUtente){
        return ResponseEntity.ok(service.disattivaUtente(idUtente));
    }

    @PutMapping("/riattiva/{idUtente}")
    public ResponseEntity<UtenteTo> riattivaUtente(@PathVariable Integer idUtente){
        return ResponseEntity.ok(service.riattivaUtente(idUtente));
    }

    @GetMapping("/cerca-per-id/{idUtente}")
    public ResponseEntity<UtenteTo> cercaUtentePerId(@PathVariable("idUtente") Integer idUtente) {
        return ResponseEntity.ok(service.getUtenteById(idUtente));
    }

    @GetMapping("/stampa-tutti")
    public ResponseEntity<List<UtenteTo>> stampaTutti() {
        return ResponseEntity.ok(service.getAllUtenti());
    }

    @DeleteMapping("/elimina-per-id/{idUtente}")
    public ResponseEntity<EsitoResponse> eliminaUtente(@PathVariable("idUtente") Integer idUtente) {
        return ResponseEntity.ok(service.deleteUtente(idUtente));
    }

}