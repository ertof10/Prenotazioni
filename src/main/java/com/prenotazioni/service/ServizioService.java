package com.prenotazioni.service;

import com.prenotazioni.dto.ServizioTo;
import com.prenotazioni.response.EsitoResponse;

import java.util.List;

public interface ServizioService {

    ServizioTo saveOrUpdateServizio(ServizioTo servizioTo);

    ServizioTo getServizioById(Integer idServizio);

    List<ServizioTo> getAllServizi();

    List<ServizioTo> getServiziAttivi();

    EsitoResponse deleteServizio(Integer idServizio);

    ServizioTo disattivaServizio(Integer idServizio);

    ServizioTo riattivaServizio(Integer idServizio);
}