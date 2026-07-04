package com.prenotazioni.Service;


import com.prenotazioni.Dto.UtenteTo;
import com.prenotazioni.Response.EsitoResponse;

import java.util.List;

public interface UtenteService {

    UtenteTo saveOrUpdateUtente(UtenteTo utenteTo);

    UtenteTo getUtenteById(Integer idUtente);

    List<UtenteTo> getAllUtenti();

    EsitoResponse deleteUtente(Integer idUtente);
}