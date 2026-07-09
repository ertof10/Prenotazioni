package com.prenotazioni.service;

import com.prenotazioni.dto.ServizioTo;
import com.prenotazioni.response.EsitoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServizioService {

    ServizioTo saveOrUpdateServizio(ServizioTo servizioTo);

    ServizioTo getServizioById(Integer idServizio);

    Page<ServizioTo> getAllServizi(Pageable pageable);

    Page<ServizioTo> getServiziAttivi(Pageable pageable);

    EsitoResponse deleteServizio(Integer idServizio);

    ServizioTo disattivaServizio(Integer idServizio);

    ServizioTo riattivaServizio(Integer idServizio);
}