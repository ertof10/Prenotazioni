package com.prenotazioni.service;

import com.prenotazioni.dto.PreventivoTo;
import com.prenotazioni.response.EsitoResponse;

import java.util.List;

public interface PreventivoService {
    PreventivoTo richiediPreventivo(PreventivoTo preventivoTo);

    PreventivoTo modificaPreventivo(PreventivoTo preventivoTo);

    PreventivoTo prendiInLavorazionePreventivo(Integer idPreventivo);

    PreventivoTo inviaPreventivo(Integer idPreventivo);

    PreventivoTo accettaPreventivo(Integer idPreventivo);

    PreventivoTo rifiutaPreventivo(Integer idPreventivo);

    PreventivoTo getPreventivoById(Integer idPreventivo);

    List<PreventivoTo> getAllPreventivi();

    List<PreventivoTo> getPreventiviByUtente(Integer idUtente);

    List<PreventivoTo> getPreventiviByStato(String statoPreventivo);

    EsitoResponse deletePreventivo(Integer idPreventivo);

    PreventivoTo chiudiManualmentePreventivo(Integer idPreventivo);
}