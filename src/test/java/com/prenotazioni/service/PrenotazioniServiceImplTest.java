package com.prenotazioni.service;

import com.prenotazioni.dao.AssenzaCollaboratoreRepository;
import com.prenotazioni.dao.CalendarioRepository;
import com.prenotazioni.dao.CollaboratoreRepository;
import com.prenotazioni.dao.CollaboratoreServizioRepository;
import com.prenotazioni.dao.PrenotazioneRepository;
import com.prenotazioni.dao.ServizioRepository;
import com.prenotazioni.dao.UtenteRepository;
import com.prenotazioni.dto.PrenotazioneTo;
import com.prenotazioni.enums.StatoPrenotazione;
import com.prenotazioni.enums.TipoGestioneServizio;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.PrenotazioneMapper;
import com.prenotazioni.po.CollaboratorePo;
import com.prenotazioni.po.CollaboratoreServizioPo;
import com.prenotazioni.po.PrenotazionePo;
import com.prenotazioni.po.ServizioPo;
import com.prenotazioni.po.UtentePo;
import com.prenotazioni.response.EsitoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrenotazioneServiceImplTest {
    @Mock
    private PrenotazioneRepository prenotazioneRepository;
    @Mock
    private UtenteRepository utenteRepository;
    @Mock
    private ServizioRepository servizioRepository;
    @Mock
    private CollaboratoreRepository collaboratoreRepository;
    @Mock
    private CollaboratoreServizioRepository collaboratoreServizioRepository;
    @Mock
    private PrenotazioneMapper prenotazioneMapper;
    @Mock
    private CalendarioRepository calendarioRepository;
    @Mock
    private AssenzaCollaboratoreRepository assenzaCollaboratoreRepository;
    private PrenotazioneServiceImpl prenotazioneService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-07-07T10:00:00Z"), ZoneId.of("Europe/Rome"));
        prenotazioneService = new PrenotazioneServiceImpl(prenotazioneRepository, utenteRepository, servizioRepository, collaboratoreRepository, collaboratoreServizioRepository, prenotazioneMapper, calendarioRepository, assenzaCollaboratoreRepository, clock);
    }

    @Test
    void saveOrUpdatePrenotazione_quandoRequestNull_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(null));
        assertEquals(AppError.PRENOTAZIONE_NON_VALIDA, exception.getError());
        verifyNoInteractions(utenteRepository);
        verifyNoInteractions(servizioRepository);
        verifyNoInteractions(prenotazioneRepository);
    }

    @Test
    void saveOrUpdatePrenotazione_quandoIdUtenteNonValido_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        prenotazioneTo.setIdUtente(0);
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(utenteRepository);
        verifyNoInteractions(servizioRepository);
    }

    @Test
    void saveOrUpdatePrenotazione_quandoDataNull_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        prenotazioneTo.setDataPrenotazione(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.PRENOTAZIONE_NON_VALIDA, exception.getError());
        verifyNoInteractions(utenteRepository);
    }

    @Test
    void saveOrUpdatePrenotazione_quandoOrarioNonValido_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        prenotazioneTo.setOraInizioPrenotazione(LocalTime.of(10, 0));
        prenotazioneTo.setOraFinePrenotazione(LocalTime.of(10, 0));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.ORARIO_PRENOTAZIONE_NON_VALIDO, exception.getError());
        verifyNoInteractions(utenteRepository);
    }

    @Test
    void saveOrUpdatePrenotazione_quandoDataNelPassato_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        prenotazioneTo.setDataPrenotazione(LocalDate.of(2026, 1, 1));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.DATA_PRENOTAZIONE_NON_VALIDA, exception.getError());
        verifyNoInteractions(utenteRepository);
    }

    @Test
    void saveOrUpdatePrenotazione_quandoUtenteNonEsiste_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.UTENTE_NON_TROVATO, exception.getError());
        verify(utenteRepository).findByIdForUpdate(3);
        verifyNoInteractions(servizioRepository);
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoUtenteDisattivo_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(false)));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.UTENTE_NON_ATTIVO, exception.getError());
        verifyNoInteractions(servizioRepository);
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoServizioNonEsiste_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findByIdForUpdate(5)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.SERVIZIO_NON_TROVATO, exception.getError());
        verify(servizioRepository).findByIdForUpdate(5);
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoServizioDisattivo_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        ServizioPo servizioPo = creaServizioAPosti(5, 2);
        servizioPo.setAttivoServizio(false);
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findByIdForUpdate(5)).thenReturn(Optional.of(servizioPo));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.SERVIZIO_NON_ATTIVO, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoTipoGestioneServizioNull_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        ServizioPo servizioPo = creaServizioAPosti(5, 2);
        servizioPo.setTipoGestioneServizio(null);
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findByIdForUpdate(5)).thenReturn(Optional.of(servizioPo));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.TIPO_GESTIONE_SERVIZIO_NON_VALIDO, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoIdPrenotazioneModificaNonValido_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        prenotazioneTo.setIdPrenotazione(0);
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findByIdForUpdate(5)).thenReturn(Optional.of(creaServizioAPosti(5, 2)));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoPrenotazioneDaModificareNonEsiste_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        prenotazioneTo.setIdPrenotazione(99);
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findByIdForUpdate(5)).thenReturn(Optional.of(creaServizioAPosti(5, 2)));
        when(prenotazioneRepository.findByIdForUpdate(99)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.PRENOTAZIONE_NON_TROVATA, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoPrenotazioneNonModificabile_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        prenotazioneTo.setIdPrenotazione(99);
        PrenotazionePo prenotazionePo = creaPrenotazionePo(99, StatoPrenotazione.ANNULLATA_DA_UTENTE);
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findByIdForUpdate(5)).thenReturn(Optional.of(creaServizioAPosti(5, 2)));
        when(prenotazioneRepository.findByIdForUpdate(99)).thenReturn(Optional.of(prenotazionePo));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.PRENOTAZIONE_NON_MODIFICABILE, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoPrenotazioneSovrappostaUtente_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        preparaUtenteEServizioAPosti();
        when(prenotazioneRepository.existsPrenotazioneSovrappostaUtente(any(), eq(3), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.PRENOTAZIONE_GIA_PRESENTE, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoServizioAPostiConCollaboratore_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        prenotazioneTo.setIdCollaboratore(7);
        preparaUtenteEServizioAPosti();
        mockNessunaSovrapposizioneUtente();
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.COLLABORATORE_NON_PREVISTO_PER_SERVIZIO_A_POSTI, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoServizioAPostiNonConfigurato_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findByIdForUpdate(5)).thenReturn(Optional.of(creaServizioAPosti(5, 0)));
        mockNessunaSovrapposizioneUtente();
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.SERVIZIO_A_POSTI_NON_CONFIGURATO, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoServizioAPostiEsaurito_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        preparaUtenteEServizioAPosti();
        mockNessunaSovrapposizioneUtente();
        when(prenotazioneRepository.countPrenotazioniConfermateServizioAPosti(any(), eq(5), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(2L);
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.POSTI_SERVIZIO_ESAURITI, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoServizioAPostiDisponibile_salvaPrenotazioneConfermata() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        preparaUtenteEServizioAPosti();
        mockNessunaSovrapposizioneUtente();
        when(prenotazioneRepository.countPrenotazioniConfermateServizioAPosti(any(), eq(5), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(0L);
        when(prenotazioneRepository.save(any(PrenotazionePo.class))).thenAnswer(invocation -> {
            PrenotazionePo prenotazionePo = invocation.getArgument(0);
            prenotazionePo.setIdPrenotazione(20);
            return prenotazionePo;
        });
        when(prenotazioneMapper.toDto(any(PrenotazionePo.class))).thenReturn(creaPrenotazioneRisposta(20, StatoPrenotazione.CONFERMATA));
        PrenotazioneTo risultato = prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo);
        assertNotNull(risultato);
        assertEquals(20, risultato.getIdPrenotazione());
        assertEquals(StatoPrenotazione.CONFERMATA.name(), risultato.getStatoPrenotazione());
        ArgumentCaptor<PrenotazionePo> captor = ArgumentCaptor.forClass(PrenotazionePo.class);
        verify(prenotazioneRepository).save(captor.capture());
        assertEquals(StatoPrenotazione.CONFERMATA, captor.getValue().getStatoPrenotazione());
        assertNull(captor.getValue().getCollaboratorePo());
        assertNotNull(captor.getValue().getDataCreazionePrenotazione());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoModificaDaRiprogrammare_salvaConfermata() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        prenotazioneTo.setIdPrenotazione(99);
        PrenotazionePo prenotazioneEsistente = creaPrenotazionePo(99, StatoPrenotazione.DA_RIPROGRAMMARE);
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findByIdForUpdate(5)).thenReturn(Optional.of(creaServizioAPosti(5, 2)));
        when(prenotazioneRepository.findByIdForUpdate(99)).thenReturn(Optional.of(prenotazioneEsistente));
        when(prenotazioneRepository.existsPrenotazioneSovrappostaUtente(eq(99), eq(3), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(prenotazioneRepository.countPrenotazioniConfermateServizioAPosti(eq(99), eq(5), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(0L);
        when(prenotazioneRepository.save(prenotazioneEsistente)).thenReturn(prenotazioneEsistente);
        when(prenotazioneMapper.toDto(prenotazioneEsistente)).thenReturn(creaPrenotazioneRisposta(99, StatoPrenotazione.CONFERMATA));
        PrenotazioneTo risultato = prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo);
        assertNotNull(risultato);
        assertEquals(StatoPrenotazione.CONFERMATA.name(), risultato.getStatoPrenotazione());
        assertEquals(StatoPrenotazione.CONFERMATA, prenotazioneEsistente.getStatoPrenotazione());
        assertNotNull(prenotazioneEsistente.getDataModificaPrenotazione());
        verify(prenotazioneRepository).save(prenotazioneEsistente);
    }

    @Test
    void saveOrUpdatePrenotazione_quandoSalvataggioNuovaFallisce_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        preparaUtenteEServizioAPosti();
        mockNessunaSovrapposizioneUtente();
        when(prenotazioneRepository.countPrenotazioniConfermateServizioAPosti(any(), eq(5), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(0L);
        when(prenotazioneRepository.save(any(PrenotazionePo.class))).thenThrow(new RuntimeException("DB error"));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.PRENOTAZIONE_CREAZIONE_FALLITA, exception.getError());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoSalvataggioModificaFallisce_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneAPostiBase();
        prenotazioneTo.setIdPrenotazione(99);
        PrenotazionePo prenotazioneEsistente = creaPrenotazionePo(99, StatoPrenotazione.CONFERMATA);
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findByIdForUpdate(5)).thenReturn(Optional.of(creaServizioAPosti(5, 2)));
        when(prenotazioneRepository.findByIdForUpdate(99)).thenReturn(Optional.of(prenotazioneEsistente));
        when(prenotazioneRepository.existsPrenotazioneSovrappostaUtente(eq(99), eq(3), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(prenotazioneRepository.countPrenotazioniConfermateServizioAPosti(eq(99), eq(5), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(0L);
        when(prenotazioneRepository.save(prenotazioneEsistente)).thenThrow(new RuntimeException("DB error"));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.PRENOTAZIONE_MODIFICA_FALLITA, exception.getError());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoCollaboratoreManualeNonEsiste_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneConCollaboratoreBase();
        preparaUtenteEServizioConCollaboratore();
        mockNessunaSovrapposizioneUtente();
        when(collaboratoreRepository.findByIdForUpdate(7)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.COLLABORATORE_NON_TROVATO, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoCollaboratoreManualeNonAttivo_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneConCollaboratoreBase();
        preparaUtenteEServizioConCollaboratore();
        mockNessunaSovrapposizioneUtente();
        when(collaboratoreRepository.findByIdForUpdate(7)).thenReturn(Optional.of(creaCollaboratore(7, false)));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.COLLABORATORE_NON_ATTIVO, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoCollaboratoreNonAbilitatoAlServizio_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneConCollaboratoreBase();
        preparaUtenteEServizioConCollaboratore();
        mockNessunaSovrapposizioneUtente();
        when(collaboratoreRepository.findByIdForUpdate(7)).thenReturn(Optional.of(creaCollaboratore(7, true)));
        when(collaboratoreServizioRepository.existsAssociazioneAttiva(7, 2)).thenReturn(false);
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.COLLABORATORE_NON_ABILITATO_AL_SERVIZIO, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoCollaboratoreFuoriCalendario_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneConCollaboratoreBase();
        preparaUtenteEServizioConCollaboratore();
        mockNessunaSovrapposizioneUtente();
        when(collaboratoreRepository.findByIdForUpdate(7)).thenReturn(Optional.of(creaCollaboratore(7, true)));
        when(collaboratoreServizioRepository.existsAssociazioneAttiva(7, 2)).thenReturn(true);
        when(calendarioRepository.existsDisponibilitaCalendario(eq(7), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.COLLABORATORE_NON_DISPONIBILE_DA_CALENDARIO, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoCollaboratoreSovrapposto_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneConCollaboratoreBase();
        preparaUtenteEServizioConCollaboratore();
        mockNessunaSovrapposizioneUtente();
        when(collaboratoreRepository.findByIdForUpdate(7)).thenReturn(Optional.of(creaCollaboratore(7, true)));
        when(collaboratoreServizioRepository.existsAssociazioneAttiva(7, 2)).thenReturn(true);
        when(calendarioRepository.existsDisponibilitaCalendario(eq(7), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
        when(prenotazioneRepository.existsPrenotazioneSovrappostaCollaboratore(any(), eq(7), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.COLLABORATORE_NON_DISPONIBILE, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoCollaboratoreAssente_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneConCollaboratoreBase();
        preparaUtenteEServizioConCollaboratore();
        mockNessunaSovrapposizioneUtente();
        when(collaboratoreRepository.findByIdForUpdate(7)).thenReturn(Optional.of(creaCollaboratore(7, true)));
        when(collaboratoreServizioRepository.existsAssociazioneAttiva(7, 2)).thenReturn(true);
        when(calendarioRepository.existsDisponibilitaCalendario(eq(7), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
        when(prenotazioneRepository.existsPrenotazioneSovrappostaCollaboratore(any(), eq(7), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(assenzaCollaboratoreRepository.existsAssenzaAttivaSuPrenotazione(eq(7), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.COLLABORATORE_ASSENTE, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoCollaboratoreManualeDisponibile_salvaConfermata() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneConCollaboratoreBase();
        CollaboratorePo collaboratorePo = creaCollaboratore(7, true);
        preparaUtenteEServizioConCollaboratore();
        mockNessunaSovrapposizioneUtente();
        when(collaboratoreRepository.findByIdForUpdate(7)).thenReturn(Optional.of(collaboratorePo));
        when(collaboratoreServizioRepository.existsAssociazioneAttiva(7, 2)).thenReturn(true);
        when(calendarioRepository.existsDisponibilitaCalendario(eq(7), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
        when(prenotazioneRepository.existsPrenotazioneSovrappostaCollaboratore(any(), eq(7), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(assenzaCollaboratoreRepository.existsAssenzaAttivaSuPrenotazione(eq(7), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(prenotazioneRepository.save(any(PrenotazionePo.class))).thenAnswer(invocation -> {
            PrenotazionePo prenotazionePo = invocation.getArgument(0);
            prenotazionePo.setIdPrenotazione(30);
            return prenotazionePo;
        });
        when(prenotazioneMapper.toDto(any(PrenotazionePo.class))).thenReturn(creaPrenotazioneRisposta(30, StatoPrenotazione.CONFERMATA));
        PrenotazioneTo risultato = prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo);
        assertNotNull(risultato);
        assertEquals(StatoPrenotazione.CONFERMATA.name(), risultato.getStatoPrenotazione());
        ArgumentCaptor<PrenotazionePo> captor = ArgumentCaptor.forClass(PrenotazionePo.class);
        verify(prenotazioneRepository).save(captor.capture());
        assertEquals(7, captor.getValue().getCollaboratorePo().getIdCollaboratore());
        assertEquals(StatoPrenotazione.CONFERMATA, captor.getValue().getStatoPrenotazione());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoAutomaticoNessunCollaboratoreDisponibile_lanciaServiceException() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneConCollaboratoreBase();
        prenotazioneTo.setIdCollaboratore(null);
        preparaUtenteEServizioConCollaboratore();
        mockNessunaSovrapposizioneUtente();
        when(collaboratoreServizioRepository.findByServizioPo_IdServizioAndAttivoCollaboratoreServizioTrue(2)).thenReturn(Collections.emptyList());
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo));
        assertEquals(AppError.NESSUN_COLLABORATORE_DISPONIBILE, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void saveOrUpdatePrenotazione_quandoAutomaticoTrovaCollaboratoreConCaricoMinore_salvaConfermata() {
        PrenotazioneTo prenotazioneTo = creaPrenotazioneConCollaboratoreBase();
        prenotazioneTo.setIdCollaboratore(null);
        CollaboratorePo collaboratore1 = creaCollaboratore(7, true);
        CollaboratorePo collaboratore2 = creaCollaboratore(8, true);
        preparaUtenteEServizioConCollaboratore();
        when(collaboratoreRepository.findByIdForUpdate(7)).thenReturn(Optional.of(collaboratore1));
        when(collaboratoreRepository.findByIdForUpdate(8)).thenReturn(Optional.of(collaboratore2));
        mockNessunaSovrapposizioneUtente();
        when(collaboratoreServizioRepository.findByServizioPo_IdServizioAndAttivoCollaboratoreServizioTrue(2)).thenReturn(Arrays.asList(creaCollaboratoreServizio(collaboratore1), creaCollaboratoreServizio(collaboratore2)));
        when(assenzaCollaboratoreRepository.existsAssenzaAttivaSuPrenotazione(anyInt(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(calendarioRepository.existsDisponibilitaCalendario(anyInt(), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
        when(prenotazioneRepository.existsPrenotazioneSovrappostaCollaboratore(any(), anyInt(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(prenotazioneRepository.countPrenotazioniCollaboratorePerData(any(), eq(7), any(LocalDate.class))).thenReturn(5L);
        when(prenotazioneRepository.countPrenotazioniCollaboratorePerData(any(), eq(8), any(LocalDate.class))).thenReturn(1L);
        when(prenotazioneRepository.save(any(PrenotazionePo.class))).thenAnswer(invocation -> {
            PrenotazionePo prenotazionePo = invocation.getArgument(0);
            prenotazionePo.setIdPrenotazione(40);
            return prenotazionePo;
        });
        when(prenotazioneMapper.toDto(any(PrenotazionePo.class))).thenReturn(creaPrenotazioneRisposta(40, StatoPrenotazione.CONFERMATA));
        PrenotazioneTo risultato = prenotazioneService.saveOrUpdatePrenotazione(prenotazioneTo);
        assertNotNull(risultato);
        ArgumentCaptor<PrenotazionePo> captor = ArgumentCaptor.forClass(PrenotazionePo.class);
        verify(prenotazioneRepository).save(captor.capture());
        assertEquals(8, captor.getValue().getCollaboratorePo().getIdCollaboratore());
        assertEquals(StatoPrenotazione.CONFERMATA, captor.getValue().getStatoPrenotazione());
    }

    @Test
    void getPrenotazioneById_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.getPrenotazioneById(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(prenotazioneRepository);
    }

    @Test
    void getPrenotazioneById_quandoNonEsiste_lanciaServiceException() {
        when(prenotazioneRepository.findById(99)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.getPrenotazioneById(99));
        assertEquals(AppError.PRENOTAZIONE_NON_TROVATA, exception.getError());
    }

    @Test
    void getPrenotazioneById_quandoEsiste_restituisceDto() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.CONFERMATA);
        PrenotazioneTo prenotazioneTo = creaPrenotazioneRisposta(1, StatoPrenotazione.CONFERMATA);
        when(prenotazioneRepository.findById(1)).thenReturn(Optional.of(prenotazionePo));
        when(prenotazioneMapper.toDto(prenotazionePo)).thenReturn(prenotazioneTo);
        PrenotazioneTo risultato = prenotazioneService.getPrenotazioneById(1);
        assertNotNull(risultato);
        assertEquals(1, risultato.getIdPrenotazione());
    }



    @Test
    void getPrenotazioniByUtente_quandoPresenti_restituisceLista() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.CONFERMATA);
        PrenotazioneTo prenotazioneTo = creaPrenotazioneRisposta(1, StatoPrenotazione.CONFERMATA);
        when(prenotazioneRepository.findByUtentePo_IdUtente(3)).thenReturn(Collections.singletonList(prenotazionePo));
        when(prenotazioneMapper.toDto(prenotazionePo)).thenReturn(prenotazioneTo);
        List<PrenotazioneTo> risultato = prenotazioneService.getPrenotazioniByUtente(3);
        assertEquals(1, risultato.size());
        assertEquals(1, risultato.get(0).getIdPrenotazione());
    }

    @Test
    void deletePrenotazione_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.deletePrenotazione(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(prenotazioneRepository);
    }

    @Test
    void deletePrenotazione_quandoNonEsiste_lanciaServiceException() {
        when(prenotazioneRepository.findByIdForUpdate(99)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> prenotazioneService.deletePrenotazione(99)
        );

        assertEquals(AppError.PRENOTAZIONE_NON_TROVATA, exception.getError());
        verify(prenotazioneRepository, never()).deleteById(anyInt());
    }

    @Test
    void deletePrenotazione_quandoEsiste_eliminaERitornaEsito() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.CONFERMATA);

        when(prenotazioneRepository.findByIdForUpdate(1)).thenReturn(Optional.of(prenotazionePo));

        EsitoResponse risultato = prenotazioneService.deletePrenotazione(1);

        assertNotNull(risultato);
        assertEquals("Prenotazione eliminata correttamente", risultato.getMessage());
        verify(prenotazioneRepository).deleteById(1);
    }

    @Test
    void deletePrenotazione_quandoDeleteFallisce_lanciaServiceException() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.CONFERMATA);

        when(prenotazioneRepository.findByIdForUpdate(1)).thenReturn(Optional.of(prenotazionePo));
        doThrow(new RuntimeException("DB error")).when(prenotazioneRepository).deleteById(1);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> prenotazioneService.deletePrenotazione(1)
        );

        assertEquals(AppError.PRENOTAZIONE_ELIMINAZIONE_FALLITA, exception.getError());
    }

    @Test
    void utenteAnnullaPrenotazione_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.utenteAnnullaPrenotazione(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(prenotazioneRepository);
    }

    @Test
    void utenteAnnullaPrenotazione_quandoNonEsiste_lanciaServiceException() {
        when(prenotazioneRepository.findByIdForUpdate(99)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.utenteAnnullaPrenotazione(99));
        assertEquals(AppError.PRENOTAZIONE_NON_TROVATA, exception.getError());
    }

    @Test
    void utenteAnnullaPrenotazione_quandoNonAnnullabile_lanciaServiceException() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.ANNULLATA_DA_STRUTTURA);
        when(prenotazioneRepository.findByIdForUpdate(1)).thenReturn(Optional.of(prenotazionePo));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.utenteAnnullaPrenotazione(1));
        assertEquals(AppError.PRENOTAZIONE_NON_ANNULLABILE, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void utenteAnnullaPrenotazione_quandoConfermata_salvaAnnullataDaUtente() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.CONFERMATA);
        when(prenotazioneRepository.findByIdForUpdate(1)).thenReturn(Optional.of(prenotazionePo));
        when(prenotazioneRepository.save(prenotazionePo)).thenReturn(prenotazionePo);
        when(prenotazioneMapper.toDto(prenotazionePo)).thenReturn(creaPrenotazioneRisposta(1, StatoPrenotazione.ANNULLATA_DA_UTENTE));
        PrenotazioneTo risultato = prenotazioneService.utenteAnnullaPrenotazione(1);
        assertNotNull(risultato);
        assertEquals(StatoPrenotazione.ANNULLATA_DA_UTENTE.name(), risultato.getStatoPrenotazione());
        assertEquals(StatoPrenotazione.ANNULLATA_DA_UTENTE, prenotazionePo.getStatoPrenotazione());
        assertNotNull(prenotazionePo.getDataModificaPrenotazione());
    }

    @Test
    void utenteAnnullaPrenotazione_quandoSaveFallisce_lanciaServiceException() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.CONFERMATA);
        when(prenotazioneRepository.findByIdForUpdate(1)).thenReturn(Optional.of(prenotazionePo));
        when(prenotazioneRepository.save(prenotazionePo)).thenThrow(new RuntimeException("DB error"));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.utenteAnnullaPrenotazione(1));
        assertEquals(AppError.PRENOTAZIONE_MODIFICA_FALLITA, exception.getError());
    }

    @Test
    void strutturaAnnullaPrenotazione_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.strutturaAnnullaPrenotazione(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(prenotazioneRepository);
    }

    @Test
    void strutturaAnnullaPrenotazione_quandoNonEsiste_lanciaServiceException() {
        when(prenotazioneRepository.findByIdForUpdate(99)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.strutturaAnnullaPrenotazione(99));
        assertEquals(AppError.PRENOTAZIONE_NON_TROVATA, exception.getError());
    }

    @Test
    void strutturaAnnullaPrenotazione_quandoNonAnnullabile_lanciaServiceException() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.ANNULLATA_DA_UTENTE);
        when(prenotazioneRepository.findByIdForUpdate(1)).thenReturn(Optional.of(prenotazionePo));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.strutturaAnnullaPrenotazione(1));
        assertEquals(AppError.PRENOTAZIONE_NON_ANNULLABILE, exception.getError());
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void strutturaAnnullaPrenotazione_quandoConfermata_salvaAnnullataDaStruttura() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.CONFERMATA);
        when(prenotazioneRepository.findByIdForUpdate(1)).thenReturn(Optional.of(prenotazionePo));
        when(prenotazioneRepository.save(prenotazionePo)).thenReturn(prenotazionePo);
        when(prenotazioneMapper.toDto(prenotazionePo)).thenReturn(creaPrenotazioneRisposta(1, StatoPrenotazione.ANNULLATA_DA_STRUTTURA));
        PrenotazioneTo risultato = prenotazioneService.strutturaAnnullaPrenotazione(1);
        assertNotNull(risultato);
        assertEquals(StatoPrenotazione.ANNULLATA_DA_STRUTTURA.name(), risultato.getStatoPrenotazione());
        assertEquals(StatoPrenotazione.ANNULLATA_DA_STRUTTURA, prenotazionePo.getStatoPrenotazione());
        assertNotNull(prenotazionePo.getDataModificaPrenotazione());
    }

    @Test
    void strutturaAnnullaPrenotazione_quandoSaveFallisce_lanciaServiceException() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.CONFERMATA);
        when(prenotazioneRepository.findByIdForUpdate(1)).thenReturn(Optional.of(prenotazionePo));
        when(prenotazioneRepository.save(prenotazionePo)).thenThrow(new RuntimeException("DB error"));
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.strutturaAnnullaPrenotazione(1));
        assertEquals(AppError.PRENOTAZIONE_MODIFICA_FALLITA, exception.getError());
    }

    @Test
    void getPrenotazioniDaRiprogrammare_quandoPresenti_restituisceLista() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.DA_RIPROGRAMMARE);
        PrenotazioneTo prenotazioneTo = creaPrenotazioneRisposta(1, StatoPrenotazione.DA_RIPROGRAMMARE);
        when(prenotazioneRepository.findByStatoPrenotazione(StatoPrenotazione.DA_RIPROGRAMMARE)).thenReturn(Collections.singletonList(prenotazionePo));
        when(prenotazioneMapper.toDto(prenotazionePo)).thenReturn(prenotazioneTo);
        List<PrenotazioneTo> risultato = prenotazioneService.getPrenotazioniDaRiprogrammare();
        assertEquals(1, risultato.size());
        assertEquals(StatoPrenotazione.DA_RIPROGRAMMARE.name(), risultato.get(0).getStatoPrenotazione());
    }

    @Test
    void getPrenotazioniDaRiprogrammareByUtente_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.getPrenotazioniDaRiprogrammareByUtente(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(utenteRepository);
    }

    @Test
    void getPrenotazioniDaRiprogrammareByUtente_quandoUtenteNonEsiste_lanciaServiceException() {
        when(utenteRepository.existsById(3)).thenReturn(false);
        ServiceException exception = assertThrows(ServiceException.class, () -> prenotazioneService.getPrenotazioniDaRiprogrammareByUtente(3));
        assertEquals(AppError.UTENTE_NON_TROVATO, exception.getError());
        verifyNoInteractions(prenotazioneMapper);
    }

    @Test
    void getPrenotazioniDaRiprogrammareByUtente_quandoPresenti_restituisceLista() {
        PrenotazionePo prenotazionePo = creaPrenotazionePo(1, StatoPrenotazione.DA_RIPROGRAMMARE);
        PrenotazioneTo prenotazioneTo = creaPrenotazioneRisposta(1, StatoPrenotazione.DA_RIPROGRAMMARE);
        when(utenteRepository.existsById(3)).thenReturn(true);
        when(prenotazioneRepository.findByUtentePo_IdUtenteAndStatoPrenotazione(3, StatoPrenotazione.DA_RIPROGRAMMARE)).thenReturn(Collections.singletonList(prenotazionePo));
        when(prenotazioneMapper.toDto(prenotazionePo)).thenReturn(prenotazioneTo);
        List<PrenotazioneTo> risultato = prenotazioneService.getPrenotazioniDaRiprogrammareByUtente(3);
        assertEquals(1, risultato.size());
        assertEquals(StatoPrenotazione.DA_RIPROGRAMMARE.name(), risultato.get(0).getStatoPrenotazione());
    }

    private void preparaUtenteEServizioAPosti() {
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findByIdForUpdate(5)).thenReturn(Optional.of(creaServizioAPosti(5, 2)));
    }

    private void preparaUtenteEServizioConCollaboratore() {
        when(utenteRepository.findByIdForUpdate(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findByIdForUpdate(2)).thenReturn(Optional.of(creaServizioConCollaboratore(2)));
    }

    private void mockNessunaSovrapposizioneUtente() {
        when(prenotazioneRepository.existsPrenotazioneSovrappostaUtente(any(), eq(3), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
    }

    private PrenotazioneTo creaPrenotazioneAPostiBase() {
        PrenotazioneTo prenotazioneTo = new PrenotazioneTo();
        prenotazioneTo.setIdUtente(3);
        prenotazioneTo.setIdServizio(5);
        prenotazioneTo.setDataPrenotazione(LocalDate.of(2027, 1, 10));
        prenotazioneTo.setOraInizioPrenotazione(LocalTime.of(18, 0));
        prenotazioneTo.setOraFinePrenotazione(LocalTime.of(19, 0));
        prenotazioneTo.setNotePrenotazione("Test prenotazione A_POSTI");
        return prenotazioneTo;
    }

    private PrenotazioneTo creaPrenotazioneConCollaboratoreBase() {
        PrenotazioneTo prenotazioneTo = new PrenotazioneTo();
        prenotazioneTo.setIdUtente(3);
        prenotazioneTo.setIdServizio(2);
        prenotazioneTo.setIdCollaboratore(7);
        prenotazioneTo.setDataPrenotazione(LocalDate.of(2027, 1, 11));
        prenotazioneTo.setOraInizioPrenotazione(LocalTime.of(9, 0));
        prenotazioneTo.setOraFinePrenotazione(LocalTime.of(9, 35));
        prenotazioneTo.setNotePrenotazione("Test prenotazione CON_COLLABORATORE");
        return prenotazioneTo;
    }

    private PrenotazioneTo creaPrenotazioneRisposta(Integer idPrenotazione, StatoPrenotazione statoPrenotazione) {
        PrenotazioneTo prenotazioneTo = new PrenotazioneTo();
        prenotazioneTo.setIdPrenotazione(idPrenotazione);
        prenotazioneTo.setStatoPrenotazione(statoPrenotazione.name());
        return prenotazioneTo;
    }

    private UtentePo creaUtente(boolean attivo) {
        UtentePo utentePo = new UtentePo();
        utentePo.setIdUtente(3);
        utentePo.setAttivoUtente(attivo);
        return utentePo;
    }

    private ServizioPo creaServizioAPosti(Integer idServizio, Integer postiMassimi) {
        ServizioPo servizioPo = new ServizioPo();
        servizioPo.setIdServizio(idServizio);
        servizioPo.setAttivoServizio(true);
        servizioPo.setTipoGestioneServizio(TipoGestioneServizio.A_POSTI);
        servizioPo.setPostiMassimiServizio(postiMassimi);
        return servizioPo;
    }

    private ServizioPo creaServizioConCollaboratore(Integer idServizio) {
        ServizioPo servizioPo = new ServizioPo();
        servizioPo.setIdServizio(idServizio);
        servizioPo.setAttivoServizio(true);
        servizioPo.setTipoGestioneServizio(TipoGestioneServizio.CON_COLLABORATORE);
        return servizioPo;
    }

    private CollaboratorePo creaCollaboratore(Integer idCollaboratore, boolean attivo) {
        CollaboratorePo collaboratorePo = new CollaboratorePo();
        collaboratorePo.setIdCollaboratore(idCollaboratore);
        collaboratorePo.setAttivoCollaboratore(attivo);
        return collaboratorePo;
    }

    private CollaboratoreServizioPo creaCollaboratoreServizio(CollaboratorePo collaboratorePo) {
        CollaboratoreServizioPo collaboratoreServizioPo = new CollaboratoreServizioPo();
        collaboratoreServizioPo.setCollaboratorePo(collaboratorePo);
        return collaboratoreServizioPo;
    }

    private PrenotazionePo creaPrenotazionePo(Integer idPrenotazione, StatoPrenotazione statoPrenotazione) {
        PrenotazionePo prenotazionePo = new PrenotazionePo();
        prenotazionePo.setIdPrenotazione(idPrenotazione);
        prenotazionePo.setStatoPrenotazione(statoPrenotazione);
        return prenotazionePo;
    }
}