package com.prenotazioni.service;

import com.prenotazioni.dao.AssenzaCollaboratoreRepository;
import com.prenotazioni.dao.CollaboratoreRepository;
import com.prenotazioni.dao.PrenotazioneRepository;
import com.prenotazioni.dto.AssenzaCollaboratoreTo;
import com.prenotazioni.enums.StatoPrenotazione;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.AssenzaCollaboratoreMapper;
import com.prenotazioni.po.AssenzaCollaboratorePo;
import com.prenotazioni.po.CollaboratorePo;
import com.prenotazioni.po.PrenotazionePo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssenzaCollaboratoreServiceImplTest {

    @Mock
    private AssenzaCollaboratoreRepository assenzaCollaboratoreRepository;

    @Mock
    private CollaboratoreRepository collaboratoreRepository;

    @Mock
    private AssenzaCollaboratoreMapper assenzaCollaboratoreMapper;

    @Mock
    private PrenotazioneRepository prenotazioneRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private AssenzaCollaboratoreServiceImpl assenzaCollaboratoreService;

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(Instant.parse("2026-07-07T10:00:00Z"));
        lenient().when(clock.getZone()).thenReturn(ZoneId.of("Europe/Rome"));
    }

    @Test
    void saveOrUpdateAssenzaCollaboratore_quandoNuovaAssenzaValida_salvaEPassaPrenotazioniADaRiprogrammare() {

        AssenzaCollaboratoreTo richiesta = creaAssenzaBase();

        CollaboratorePo collaboratorePo = new CollaboratorePo();
        collaboratorePo.setIdCollaboratore(3);

        PrenotazionePo prenotazionePo = new PrenotazionePo();
        prenotazionePo.setIdPrenotazione(10);
        prenotazionePo.setStatoPrenotazione(StatoPrenotazione.CONFERMATA);

        AssenzaCollaboratoreTo risposta = new AssenzaCollaboratoreTo();
        risposta.setIdAssenzaCollaboratore(1);
        risposta.setIdCollaboratore(3);
        risposta.setAttivoAssenzaCollaboratore(true);

        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));

        when(assenzaCollaboratoreRepository.existsAssenzaSovrapposta(
                any(),
                eq(3),
                any(LocalDate.class),
                any(LocalDate.class),
                any(LocalTime.class),
                any(LocalTime.class)
        )).thenReturn(false);

        when(assenzaCollaboratoreRepository.save(any(AssenzaCollaboratorePo.class)))
                .thenAnswer(invocation -> {
                    AssenzaCollaboratorePo assenzaCollaboratorePo = invocation.getArgument(0);
                    assenzaCollaboratorePo.setIdAssenzaCollaboratore(1);
                    return assenzaCollaboratorePo;
                });

        when(prenotazioneRepository.findPrenotazioniConfermateCheHannoAssenza(
                eq(3),
                any(LocalDate.class),
                any(LocalDate.class),
                any(LocalTime.class),
                any(LocalTime.class)
        )).thenReturn(Collections.singletonList(prenotazionePo));

        when(assenzaCollaboratoreMapper.toDto(any(AssenzaCollaboratorePo.class))).thenReturn(risposta);

        AssenzaCollaboratoreTo risultato =
                assenzaCollaboratoreService.saveOrUpdateAssenzaCollaboratore(richiesta);

        assertNotNull(risultato);
        assertEquals(1, risultato.getIdAssenzaCollaboratore());
        assertEquals(StatoPrenotazione.DA_RIPROGRAMMARE, prenotazionePo.getStatoPrenotazione());

        verify(assenzaCollaboratoreRepository).save(any(AssenzaCollaboratorePo.class));
        verify(prenotazioneRepository).saveAll(Collections.singletonList(prenotazionePo));
    }

    @Test
    void saveOrUpdateAssenzaCollaboratore_quandoAssenzaSovrapposta_lanciaServiceException() {

        AssenzaCollaboratoreTo richiesta = creaAssenzaBase();

        CollaboratorePo collaboratorePo = new CollaboratorePo();
        collaboratorePo.setIdCollaboratore(3);

        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));

        when(assenzaCollaboratoreRepository.existsAssenzaSovrapposta(
                any(),
                eq(3),
                any(LocalDate.class),
                any(LocalDate.class),
                any(LocalTime.class),
                any(LocalTime.class)
        )).thenReturn(true);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> assenzaCollaboratoreService.saveOrUpdateAssenzaCollaboratore(richiesta)
        );

        assertEquals(AppError.ASSENZA_COLLABORATORE_GIA_PRESENTE, exception.getError());

        verify(assenzaCollaboratoreRepository, never()).save(any());
        verify(prenotazioneRepository, never()).saveAll(any());
    }

    @Test
    void saveOrUpdateAssenzaCollaboratore_quandoIdCollaboratoreNonValido_lanciaServiceException() {

        AssenzaCollaboratoreTo richiesta = creaAssenzaBase();
        richiesta.setIdCollaboratore(0);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> assenzaCollaboratoreService.saveOrUpdateAssenzaCollaboratore(richiesta)
        );

        assertEquals(AppError.ID_NON_VALIDO, exception.getError());

        verifyNoInteractions(collaboratoreRepository);
        verifyNoInteractions(assenzaCollaboratoreRepository);
    }

    private AssenzaCollaboratoreTo creaAssenzaBase() {

        AssenzaCollaboratoreTo assenzaCollaboratoreTo = new AssenzaCollaboratoreTo();
        assenzaCollaboratoreTo.setIdCollaboratore(3);
        assenzaCollaboratoreTo.setDataInizioAssenza(LocalDate.of(2027, 2, 10));
        assenzaCollaboratoreTo.setDataFineAssenza(LocalDate.of(2027, 2, 10));
        assenzaCollaboratoreTo.setOraInizioAssenza(LocalTime.of(9, 0));
        assenzaCollaboratoreTo.setOraFineAssenza(LocalTime.of(13, 0));
        assenzaCollaboratoreTo.setMotivoAssenzaCollaboratore("Permesso");

        return assenzaCollaboratoreTo;
    }
}