package com.prenotazioni.service;

import com.prenotazioni.dto.PreventivoTo;
import com.prenotazioni.response.EsitoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PreventivoService {

    PreventivoTo richiediPreventivo(PreventivoTo preventivoTo);

    PreventivoTo modificaPreventivo(PreventivoTo preventivoTo);

    PreventivoTo prendiInLavorazionePreventivo(Integer idPreventivo);

    PreventivoTo inviaPreventivo(Integer idPreventivo);

    PreventivoTo accettaPreventivo(Integer idPreventivo);

    PreventivoTo rifiutaPreventivo(Integer idPreventivo);

    PreventivoTo getPreventivoById(Integer idPreventivo);

    Page<PreventivoTo> getAllPreventivi(Pageable pageable);

    Page<PreventivoTo> getPreventiviByUtente(Integer idUtente, Pageable pageable);

    Page<PreventivoTo> getPreventiviByStato(String statoPreventivo, Pageable pageable);

    EsitoResponse deletePreventivo(Integer idPreventivo);

    PreventivoTo chiudiManualmentePreventivo(Integer idPreventivo);
}