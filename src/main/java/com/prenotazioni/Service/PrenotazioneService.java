package com.prenotazioni.Service;

import com.prenotazioni.Dto.PrenotazioneTo;
import com.prenotazioni.Response.EsitoResponse;

import java.util.List;

public interface PrenotazioneService {

    PrenotazioneTo saveOrUpdatePrenotazione(PrenotazioneTo prenotazioneTo);

    PrenotazioneTo getPrenotazioneById(Integer idPrenotazione);

    List<PrenotazioneTo> getAllPrenotazioni();

    List<PrenotazioneTo> getPrenotazioniByUtente(Integer idUtente);

    EsitoResponse deletePrenotazione(Integer idPrenotazione);

    PrenotazioneTo annullaPrenotazione(Integer idPrenotazione);
}