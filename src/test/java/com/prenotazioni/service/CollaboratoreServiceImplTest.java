package com.prenotazioni.service;

import com.prenotazioni.dao.AssenzaCollaboratoreRepository;
import com.prenotazioni.dao.CalendarioRepository;
import com.prenotazioni.dao.CollaboratoreRepository;
import com.prenotazioni.dao.CollaboratoreServizioRepository;
import com.prenotazioni.dao.PrenotazioneRepository;
import com.prenotazioni.dto.CollaboratoreTo;
import com.prenotazioni.enums.StatoPrenotazione;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.CollaboratoreMapper;
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
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollaboratoreServiceImplTest {

    @Mock
    private CollaboratoreRepository collaboratoreRepository;

    @Mock
    private CollaboratoreMapper collaboratoreMapper;

    @Mock
    private PrenotazioneRepository prenotazioneRepository;

    @Mock
    private CollaboratoreServizioRepository collaboratoreServizioRepository;

    @Mock
    private CalendarioRepository calendarioRepository;

    @Mock
    private AssenzaCollaboratoreRepository assenzaCollaboratoreRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private CollaboratoreServiceImpl collaboratoreService;

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(Instant.parse("2026-07-07T10:00:00Z"));
        lenient().when(clock.getZone()).thenReturn(ZoneId.of("Europe/Rome"));
    }

    @Test
    void disattivaCollaboratore_quandoEsiste_impostaInattivoERiprogrammaPrenotazioniFuture() {

        Integer idCollaboratore = 3;

        CollaboratorePo collaboratorePo = new CollaboratorePo();
        collaboratorePo.setIdCollaboratore(idCollaboratore);
        collaboratorePo.setAttivoCollaboratore(true);

        PrenotazionePo prenotazionePo = new PrenotazionePo();
        prenotazionePo.setIdPrenotazione(10);
        prenotazionePo.setStatoPrenotazione(StatoPrenotazione.CONFERMATA);

        CollaboratoreTo collaboratoreTo = new CollaboratoreTo();
        collaboratoreTo.setIdCollaboratore(idCollaboratore);
        collaboratoreTo.setAttivoCollaboratore(false);

        when(collaboratoreRepository.findById(idCollaboratore)).thenReturn(Optional.of(collaboratorePo));

        when(prenotazioneRepository.findPrenotazioniFutureConfermateByCollaboratore(
                eq(idCollaboratore),
                any(LocalDate.class)
        )).thenReturn(Collections.singletonList(prenotazionePo));

        when(collaboratoreRepository.save(collaboratorePo)).thenReturn(collaboratorePo);
        when(collaboratoreMapper.toDto(collaboratorePo)).thenReturn(collaboratoreTo);

        CollaboratoreTo risultato = collaboratoreService.disattivaCollaboratore(idCollaboratore);

        assertNotNull(risultato);
        assertFalse(risultato.getAttivoCollaboratore());
        assertFalse(collaboratorePo.getAttivoCollaboratore());
        assertEquals(StatoPrenotazione.DA_RIPROGRAMMARE, prenotazionePo.getStatoPrenotazione());

        verify(prenotazioneRepository).saveAll(Collections.singletonList(prenotazionePo));
        verify(collaboratoreRepository).save(collaboratorePo);
    }

    @Test
    void riattivaCollaboratore_quandoEsiste_impostaAttivoTrue() {

        Integer idCollaboratore = 3;

        CollaboratorePo collaboratorePo = new CollaboratorePo();
        collaboratorePo.setIdCollaboratore(idCollaboratore);
        collaboratorePo.setAttivoCollaboratore(false);

        CollaboratoreTo collaboratoreTo = new CollaboratoreTo();
        collaboratoreTo.setIdCollaboratore(idCollaboratore);
        collaboratoreTo.setAttivoCollaboratore(true);

        when(collaboratoreRepository.findById(idCollaboratore)).thenReturn(Optional.of(collaboratorePo));
        when(collaboratoreRepository.save(collaboratorePo)).thenReturn(collaboratorePo);
        when(collaboratoreMapper.toDto(collaboratorePo)).thenReturn(collaboratoreTo);

        CollaboratoreTo risultato = collaboratoreService.riattivaCollaboratore(idCollaboratore);

        assertNotNull(risultato);
        assertTrue(risultato.getAttivoCollaboratore());
        assertTrue(collaboratorePo.getAttivoCollaboratore());

        verify(collaboratoreRepository).findById(idCollaboratore);
        verify(collaboratoreRepository).save(collaboratorePo);
    }

    @Test
    void disattivaCollaboratore_quandoIdNonValido_lanciaServiceException() {

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> collaboratoreService.disattivaCollaboratore(0)
        );

        assertEquals(AppError.ID_NON_VALIDO, exception.getError());

        verifyNoInteractions(collaboratoreRepository);
    }
}