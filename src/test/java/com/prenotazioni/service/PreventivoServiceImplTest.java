package com.prenotazioni.service;

import com.prenotazioni.dao.PreventivoRepository;
import com.prenotazioni.dao.ServizioRepository;
import com.prenotazioni.dao.UtenteRepository;
import com.prenotazioni.dto.PreventivoTo;
import com.prenotazioni.enums.StatoPreventivo;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.PreventivoMapper;
import com.prenotazioni.po.PreventivoPo;
import com.prenotazioni.po.ServizioPo;
import com.prenotazioni.po.UtentePo;
import com.prenotazioni.response.EsitoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreventivoServiceImplTest {
    @Mock
    private PreventivoRepository preventivoRepository;
    @Mock
    private UtenteRepository utenteRepository;
    @Mock
    private ServizioRepository servizioRepository;
    @Mock
    private PreventivoMapper preventivoMapper;
    private PreventivoServiceImpl preventivoService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-07-07T10:00:00Z"), ZoneId.of("Europe/Rome"));
        preventivoService = new PreventivoServiceImpl(preventivoRepository, utenteRepository, servizioRepository, preventivoMapper, clock);
    }

    @Test
    void richiediPreventivo_quandoRequestNull_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.richiediPreventivo(null));
        assertEquals(AppError.PREVENTIVO_NON_VALIDO, exception.getError());
        verifyNoInteractions(utenteRepository);
        verifyNoInteractions(servizioRepository);
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void richiediPreventivo_quandoIdUtenteNonValido_lanciaServiceException() {
        PreventivoTo preventivoTo = creaRichiestaPreventivoBase();
        preventivoTo.setIdUtente(0);
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.richiediPreventivo(preventivoTo));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(utenteRepository);
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void richiediPreventivo_quandoTitoloVuoto_lanciaServiceException() {
        PreventivoTo preventivoTo = creaRichiestaPreventivoBase();
        preventivoTo.setTitoloPreventivo(" ");
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.richiediPreventivo(preventivoTo));
        assertEquals(AppError.PREVENTIVO_NON_VALIDO, exception.getError());
        verifyNoInteractions(utenteRepository);
    }

    @Test
    void richiediPreventivo_quandoDescrizioneVuota_lanciaServiceException() {
        PreventivoTo preventivoTo = creaRichiestaPreventivoBase();
        preventivoTo.setDescrizioneRichiestaPreventivo(" ");
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.richiediPreventivo(preventivoTo));
        assertEquals(AppError.PREVENTIVO_NON_VALIDO, exception.getError());
        verifyNoInteractions(utenteRepository);
    }

    @Test
    void richiediPreventivo_quandoUtenteNonEsiste_lanciaServiceException() {
        PreventivoTo preventivoTo = creaRichiestaPreventivoBase();
        when(utenteRepository.findById(3)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.richiediPreventivo(preventivoTo));
        assertEquals(AppError.UTENTE_NON_TROVATO, exception.getError());
        verify(utenteRepository).findById(3);
        verifyNoInteractions(servizioRepository);
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void richiediPreventivo_quandoUtenteNonAttivo_lanciaServiceException() {
        PreventivoTo preventivoTo = creaRichiestaPreventivoBase();
        when(utenteRepository.findById(3)).thenReturn(Optional.of(creaUtente(false)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.richiediPreventivo(preventivoTo));
        assertEquals(AppError.UTENTE_NON_ATTIVO, exception.getError());
        verifyNoInteractions(servizioRepository);
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void richiediPreventivo_quandoIdServizioNonValido_lanciaServiceException() {
        PreventivoTo preventivoTo = creaRichiestaPreventivoBase();
        preventivoTo.setIdServizio(0);
        when(utenteRepository.findById(3)).thenReturn(Optional.of(creaUtente(true)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.richiediPreventivo(preventivoTo));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void richiediPreventivo_quandoServizioNonEsiste_lanciaServiceException() {
        PreventivoTo preventivoTo = creaRichiestaPreventivoBase();
        preventivoTo.setIdServizio(2);
        when(utenteRepository.findById(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findById(2)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.richiediPreventivo(preventivoTo));
        assertEquals(AppError.SERVIZIO_NON_TROVATO, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void richiediPreventivo_quandoServizioNonAttivo_lanciaServiceException() {
        PreventivoTo preventivoTo = creaRichiestaPreventivoBase();
        preventivoTo.setIdServizio(2);
        when(utenteRepository.findById(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findById(2)).thenReturn(Optional.of(creaServizio(false)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.richiediPreventivo(preventivoTo));
        assertEquals(AppError.SERVIZIO_NON_ATTIVO, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void richiediPreventivo_quandoValidoSenzaServizio_salvaRichiesto() {
        PreventivoTo preventivoTo = creaRichiestaPreventivoBase();
        preventivoTo.setTitoloPreventivo(" Titolo test ");
        preventivoTo.setDescrizioneRichiestaPreventivo(" Descrizione test ");
        when(utenteRepository.findById(3)).thenReturn(Optional.of(creaUtente(true)));
        when(preventivoRepository.save(any(PreventivoPo.class))).thenAnswer(invocation -> {
            PreventivoPo preventivoPo = invocation.getArgument(0);
            preventivoPo.setIdPreventivo(1);
            return preventivoPo;
        });
        when(preventivoMapper.toDto(any(PreventivoPo.class))).thenReturn(creaPreventivoRisposta(1));
        PreventivoTo risultato = preventivoService.richiediPreventivo(preventivoTo);
        assertNotNull(risultato);
        assertEquals(1, risultato.getIdPreventivo());
        ArgumentCaptor<PreventivoPo> captor = ArgumentCaptor.forClass(PreventivoPo.class);
        verify(preventivoRepository).save(captor.capture());
        assertEquals(StatoPreventivo.RICHIESTO, captor.getValue().getStatoPreventivo());
        assertEquals("Titolo test", captor.getValue().getTitoloPreventivo());
        assertEquals("Descrizione test", captor.getValue().getDescrizioneRichiestaPreventivo());
        assertNull(captor.getValue().getServizioPo());
        assertNull(captor.getValue().getImportoPreventivo());
        assertNull(captor.getValue().getRispostaPreventivo());
        assertNull(captor.getValue().getDataScadenzaPreventivo());
        assertNotNull(captor.getValue().getDataCreazionePreventivo());
    }

    @Test
    void richiediPreventivo_quandoValidoConServizio_salvaRichiestoConServizio() {
        PreventivoTo preventivoTo = creaRichiestaPreventivoBase();
        preventivoTo.setIdServizio(2);
        UtentePo utentePo = creaUtente(true);
        ServizioPo servizioPo = creaServizio(true);
        when(utenteRepository.findById(3)).thenReturn(Optional.of(utentePo));
        when(servizioRepository.findById(2)).thenReturn(Optional.of(servizioPo));
        when(preventivoRepository.save(any(PreventivoPo.class))).thenAnswer(invocation -> {
            PreventivoPo preventivoPo = invocation.getArgument(0);
            preventivoPo.setIdPreventivo(1);
            return preventivoPo;
        });
        when(preventivoMapper.toDto(any(PreventivoPo.class))).thenReturn(creaPreventivoRisposta(1));
        PreventivoTo risultato = preventivoService.richiediPreventivo(preventivoTo);
        assertNotNull(risultato);
        ArgumentCaptor<PreventivoPo> captor = ArgumentCaptor.forClass(PreventivoPo.class);
        verify(preventivoRepository).save(captor.capture());
        assertEquals(utentePo, captor.getValue().getUtentePo());
        assertEquals(servizioPo, captor.getValue().getServizioPo());
        assertEquals(StatoPreventivo.RICHIESTO, captor.getValue().getStatoPreventivo());
    }

    @Test
    void richiediPreventivo_quandoSaveFallisce_lanciaServiceException() {
        PreventivoTo preventivoTo = creaRichiestaPreventivoBase();
        when(utenteRepository.findById(3)).thenReturn(Optional.of(creaUtente(true)));
        when(preventivoRepository.save(any(PreventivoPo.class))).thenThrow(new RuntimeException("DB error"));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.richiediPreventivo(preventivoTo));
        assertEquals(AppError.PREVENTIVO_CREAZIONE_FALLITA, exception.getError());
    }

    @Test
    void modificaPreventivo_quandoRequestNull_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.modificaPreventivo(null));
        assertEquals(AppError.PREVENTIVO_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void modificaPreventivo_quandoIdPreventivoNonValido_lanciaServiceException() {
        PreventivoTo preventivoTo = creaModificaPreventivoBase();
        preventivoTo.setIdPreventivo(0);
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.modificaPreventivo(preventivoTo));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void modificaPreventivo_quandoIdUtenteNonValido_lanciaServiceException() {
        PreventivoTo preventivoTo = creaModificaPreventivoBase();
        preventivoTo.setIdUtente(0);
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.modificaPreventivo(preventivoTo));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void modificaPreventivo_quandoPreventivoNonEsiste_lanciaServiceException() {
        PreventivoTo preventivoTo = creaModificaPreventivoBase();
        when(preventivoRepository.findById(1)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.modificaPreventivo(preventivoTo));
        assertEquals(AppError.PREVENTIVO_NON_TROVATO, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void modificaPreventivo_quandoPreventivoAccettato_lanciaServiceException() {
        PreventivoTo preventivoTo = creaModificaPreventivoBase();
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(creaPreventivoPo(1, StatoPreventivo.ACCETTATO)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.modificaPreventivo(preventivoTo));
        assertEquals(AppError.PREVENTIVO_NON_MODIFICABILE, exception.getError());
        verifyNoInteractions(utenteRepository);
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void modificaPreventivo_quandoUtenteNonEsiste_lanciaServiceException() {
        PreventivoTo preventivoTo = creaModificaPreventivoBase();
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(creaPreventivoPo(1, StatoPreventivo.RICHIESTO)));
        when(utenteRepository.findById(3)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.modificaPreventivo(preventivoTo));
        assertEquals(AppError.UTENTE_NON_TROVATO, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void modificaPreventivo_quandoIdServizioNonValido_lanciaServiceException() {
        PreventivoTo preventivoTo = creaModificaPreventivoBase();
        preventivoTo.setIdServizio(0);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(creaPreventivoPo(1, StatoPreventivo.RICHIESTO)));
        when(utenteRepository.findById(3)).thenReturn(Optional.of(creaUtente(true)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.modificaPreventivo(preventivoTo));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void modificaPreventivo_quandoServizioNonEsiste_lanciaServiceException() {
        PreventivoTo preventivoTo = creaModificaPreventivoBase();
        preventivoTo.setIdServizio(2);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(creaPreventivoPo(1, StatoPreventivo.RICHIESTO)));
        when(utenteRepository.findById(3)).thenReturn(Optional.of(creaUtente(true)));
        when(servizioRepository.findById(2)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.modificaPreventivo(preventivoTo));
        assertEquals(AppError.SERVIZIO_NON_TROVATO, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void modificaPreventivo_quandoTitoloVuoto_lanciaServiceException() {
        PreventivoTo preventivoTo = creaModificaPreventivoBase();
        preventivoTo.setTitoloPreventivo(" ");
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(creaPreventivoPo(1, StatoPreventivo.RICHIESTO)));
        when(utenteRepository.findById(3)).thenReturn(Optional.of(creaUtente(true)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.modificaPreventivo(preventivoTo));
        assertEquals(AppError.PREVENTIVO_NON_VALIDO, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void modificaPreventivo_quandoImportoNonValido_lanciaServiceException() {
        PreventivoTo preventivoTo = creaModificaPreventivoBase();
        preventivoTo.setImportoPreventivo(BigDecimal.ZERO);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(creaPreventivoPo(1, StatoPreventivo.RICHIESTO)));
        when(utenteRepository.findById(3)).thenReturn(Optional.of(creaUtente(true)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.modificaPreventivo(preventivoTo));
        assertEquals(AppError.IMPORTO_PREVENTIVO_NON_VALIDO, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void modificaPreventivo_quandoValido_salvaModifica() {
        PreventivoTo preventivoTo = creaModificaPreventivoBase();
        preventivoTo.setIdServizio(2);
        preventivoTo.setTitoloPreventivo(" Nuovo titolo ");
        preventivoTo.setDescrizioneRichiestaPreventivo(" Nuova descrizione ");
        preventivoTo.setImportoPreventivo(BigDecimal.valueOf(200));
        preventivoTo.setRispostaPreventivo("Risposta tecnica");
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.IN_LAVORAZIONE);
        UtentePo utentePo = creaUtente(true);
        ServizioPo servizioPo = creaServizio(true);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        when(utenteRepository.findById(3)).thenReturn(Optional.of(utentePo));
        when(servizioRepository.findById(2)).thenReturn(Optional.of(servizioPo));
        when(preventivoRepository.save(preventivoPo)).thenReturn(preventivoPo);
        when(preventivoMapper.toDto(preventivoPo)).thenReturn(creaPreventivoRisposta(1));
        PreventivoTo risultato = preventivoService.modificaPreventivo(preventivoTo);
        assertNotNull(risultato);
        assertEquals(1, risultato.getIdPreventivo());
        assertEquals(utentePo, preventivoPo.getUtentePo());
        assertEquals(servizioPo, preventivoPo.getServizioPo());
        assertEquals("Nuovo titolo", preventivoPo.getTitoloPreventivo());
        assertEquals("Nuova descrizione", preventivoPo.getDescrizioneRichiestaPreventivo());
        assertEquals(BigDecimal.valueOf(200), preventivoPo.getImportoPreventivo());
        assertEquals("Risposta tecnica", preventivoPo.getRispostaPreventivo());
        assertNotNull(preventivoPo.getDataModificaPreventivo());
    }

    @Test
    void modificaPreventivo_quandoSaveFallisce_lanciaServiceException() {
        PreventivoTo preventivoTo = creaModificaPreventivoBase();
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.RICHIESTO);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        when(utenteRepository.findById(3)).thenReturn(Optional.of(creaUtente(true)));
        when(preventivoRepository.save(preventivoPo)).thenThrow(new RuntimeException("DB error"));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.modificaPreventivo(preventivoTo));
        assertEquals(AppError.PREVENTIVO_MODIFICA_FALLITA, exception.getError());
    }

    @Test
    void prendiInLavorazionePreventivo_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.prendiInLavorazionePreventivo(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void prendiInLavorazionePreventivo_quandoNonEsiste_lanciaServiceException() {
        when(preventivoRepository.findById(1)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.prendiInLavorazionePreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_TROVATO, exception.getError());
    }

    @Test
    void prendiInLavorazionePreventivo_quandoStatoNonRichiesto_lanciaServiceException() {
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(creaPreventivoPo(1, StatoPreventivo.IN_LAVORAZIONE)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.prendiInLavorazionePreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_MODIFICABILE, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void prendiInLavorazionePreventivo_quandoRichiesto_salvaInLavorazione() {
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.RICHIESTO);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        when(preventivoRepository.save(preventivoPo)).thenReturn(preventivoPo);
        when(preventivoMapper.toDto(preventivoPo)).thenReturn(creaPreventivoRisposta(1));
        PreventivoTo risultato = preventivoService.prendiInLavorazionePreventivo(1);
        assertNotNull(risultato);
        assertEquals(StatoPreventivo.IN_LAVORAZIONE, preventivoPo.getStatoPreventivo());
        assertNotNull(preventivoPo.getDataModificaPreventivo());
    }

    @Test
    void prendiInLavorazionePreventivo_quandoSaveFallisce_lanciaServiceException() {
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.RICHIESTO);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        when(preventivoRepository.save(preventivoPo)).thenThrow(new RuntimeException("DB error"));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.prendiInLavorazionePreventivo(1));
        assertEquals(AppError.PREVENTIVO_MODIFICA_FALLITA, exception.getError());
    }

    @Test
    void inviaPreventivo_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.inviaPreventivo(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void inviaPreventivo_quandoNonEsiste_lanciaServiceException() {
        when(preventivoRepository.findById(1)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.inviaPreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_TROVATO, exception.getError());
    }

    @Test
    void inviaPreventivo_quandoStatoNonInviabile_lanciaServiceException() {
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(creaPreventivoPo(1, StatoPreventivo.ACCETTATO)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.inviaPreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_INVIABILE, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void inviaPreventivo_quandoImportoNull_lanciaServiceException() {
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.IN_LAVORAZIONE);
        preventivoPo.setImportoPreventivo(null);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.inviaPreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_INVIABILE, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void inviaPreventivo_quandoImportoZero_lanciaServiceException() {
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.IN_LAVORAZIONE);
        preventivoPo.setImportoPreventivo(BigDecimal.ZERO);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.inviaPreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_INVIABILE, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void inviaPreventivo_quandoValido_salvaInviato() {
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.IN_LAVORAZIONE);
        preventivoPo.setImportoPreventivo(BigDecimal.valueOf(100));
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        when(preventivoRepository.save(preventivoPo)).thenReturn(preventivoPo);
        when(preventivoMapper.toDto(preventivoPo)).thenReturn(creaPreventivoRisposta(1));
        PreventivoTo risultato = preventivoService.inviaPreventivo(1);
        assertNotNull(risultato);
        assertEquals(StatoPreventivo.INVIATO, preventivoPo.getStatoPreventivo());
        assertNotNull(preventivoPo.getDataModificaPreventivo());
    }

    @Test
    void inviaPreventivo_quandoSaveFallisce_lanciaServiceException() {
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.IN_LAVORAZIONE);
        preventivoPo.setImportoPreventivo(BigDecimal.valueOf(100));
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        when(preventivoRepository.save(preventivoPo)).thenThrow(new RuntimeException("DB error"));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.inviaPreventivo(1));
        assertEquals(AppError.PREVENTIVO_MODIFICA_FALLITA, exception.getError());
    }

    @Test
    void accettaPreventivo_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.accettaPreventivo(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void accettaPreventivo_quandoNonEsiste_lanciaServiceException() {
        when(preventivoRepository.findById(1)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.accettaPreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_TROVATO, exception.getError());
    }

    @Test
    void accettaPreventivo_quandoNonInviato_lanciaServiceException() {
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(creaPreventivoPo(1, StatoPreventivo.RICHIESTO)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.accettaPreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_ACCETTABILE, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void accettaPreventivo_quandoInviato_salvaAccettato() {
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.INVIATO);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        when(preventivoRepository.save(preventivoPo)).thenReturn(preventivoPo);
        when(preventivoMapper.toDto(preventivoPo)).thenReturn(creaPreventivoRisposta(1));
        PreventivoTo risultato = preventivoService.accettaPreventivo(1);
        assertNotNull(risultato);
        assertEquals(StatoPreventivo.ACCETTATO, preventivoPo.getStatoPreventivo());
        assertNotNull(preventivoPo.getDataModificaPreventivo());
    }

    @Test
    void rifiutaPreventivo_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.rifiutaPreventivo(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void rifiutaPreventivo_quandoNonEsiste_lanciaServiceException() {
        when(preventivoRepository.findById(1)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.rifiutaPreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_TROVATO, exception.getError());
    }

    @Test
    void rifiutaPreventivo_quandoNonInviato_lanciaServiceException() {
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(creaPreventivoPo(1, StatoPreventivo.RICHIESTO)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.rifiutaPreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_RIFIUTABILE, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void rifiutaPreventivo_quandoInviato_salvaRifiutato() {
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.INVIATO);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        when(preventivoRepository.save(preventivoPo)).thenReturn(preventivoPo);
        when(preventivoMapper.toDto(preventivoPo)).thenReturn(creaPreventivoRisposta(1));
        PreventivoTo risultato = preventivoService.rifiutaPreventivo(1);
        assertNotNull(risultato);
        assertEquals(StatoPreventivo.RIFIUTATO, preventivoPo.getStatoPreventivo());
        assertNotNull(preventivoPo.getDataModificaPreventivo());
    }

    @Test
    void getPreventivoById_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.getPreventivoById(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void getPreventivoById_quandoNonEsiste_lanciaServiceException() {
        when(preventivoRepository.findById(1)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.getPreventivoById(1));
        assertEquals(AppError.PREVENTIVO_NON_TROVATO, exception.getError());
    }

    @Test
    void getPreventivoById_quandoEsiste_restituisceDto() {
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.RICHIESTO);
        PreventivoTo preventivoTo = creaPreventivoRisposta(1);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        when(preventivoMapper.toDto(preventivoPo)).thenReturn(preventivoTo);
        PreventivoTo risultato = preventivoService.getPreventivoById(1);
        assertNotNull(risultato);
        assertEquals(1, risultato.getIdPreventivo());
    }

    @Test
    void getAllPreventivi_quandoPresenti_restituiscePagina() {
        Pageable pageable = PageRequest.of(0, 10);

        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.RICHIESTO);
        PreventivoTo preventivoTo = creaPreventivoRisposta(1);

        when(preventivoRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(preventivoPo), pageable, 1));

        when(preventivoMapper.toDto(preventivoPo)).thenReturn(preventivoTo);

        Page<PreventivoTo> risultato = preventivoService.getAllPreventivi(pageable);

        assertEquals(1, risultato.getTotalElements());
        assertEquals(1, risultato.getContent().size());
        assertEquals(1, risultato.getContent().get(0).getIdPreventivo());

        verify(preventivoRepository).findAll(pageable);
    }

    @Test
    void getPreventiviByUtente_quandoIdNonValido_lanciaServiceException() {
        Pageable pageable = PageRequest.of(0, 10);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> preventivoService.getPreventiviByUtente(0, pageable)
        );

        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void getPreventiviByUtente_quandoUtenteNonEsiste_lanciaServiceException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(utenteRepository.existsById(3)).thenReturn(false);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> preventivoService.getPreventiviByUtente(3, pageable)
        );

        assertEquals(AppError.UTENTE_NON_TROVATO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void getPreventiviByUtente_quandoPresenti_restituiscePagina() {
        Pageable pageable = PageRequest.of(0, 10);

        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.RICHIESTO);
        PreventivoTo preventivoTo = creaPreventivoRisposta(1);

        when(utenteRepository.existsById(3)).thenReturn(true);

        when(preventivoRepository.findByUtentePo_IdUtente(3, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(preventivoPo), pageable, 1));

        when(preventivoMapper.toDto(preventivoPo)).thenReturn(preventivoTo);

        Page<PreventivoTo> risultato = preventivoService.getPreventiviByUtente(3, pageable);

        assertEquals(1, risultato.getTotalElements());
        assertEquals(1, risultato.getContent().size());
        assertEquals(1, risultato.getContent().get(0).getIdPreventivo());

        verify(preventivoRepository).findByUtentePo_IdUtente(3, pageable);
    }

    @Test
    void getPreventiviByStato_quandoStatoNonValido_lanciaServiceException() {
        Pageable pageable = PageRequest.of(0, 10);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> preventivoService.getPreventiviByStato("stato_sbagliato", pageable)
        );

        assertEquals(AppError.STATO_PREVENTIVO_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }



    @Test
    void deletePreventivo_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.deletePreventivo(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void deletePreventivo_quandoNonEsiste_lanciaServiceException() {
        when(preventivoRepository.existsById(1)).thenReturn(false);
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.deletePreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_TROVATO, exception.getError());
        verify(preventivoRepository, never()).deleteById(anyInt());
    }

    @Test
    void deletePreventivo_quandoEsiste_eliminaERitornaEsito() {
        when(preventivoRepository.existsById(1)).thenReturn(true);
        EsitoResponse risultato = preventivoService.deletePreventivo(1);
        assertNotNull(risultato);
        assertEquals("Preventivo eliminato correttamente", risultato.getMessage());
        verify(preventivoRepository).deleteById(1);
    }

    @Test
    void deletePreventivo_quandoDeleteFallisce_lanciaServiceException() {
        when(preventivoRepository.existsById(1)).thenReturn(true);
        doThrow(new RuntimeException("DB error")).when(preventivoRepository).deleteById(1);
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.deletePreventivo(1));
        assertEquals(AppError.PREVENTIVO_ELIMINAZIONE_FALLITA, exception.getError());
    }

    @Test
    void chiudiManualmentePreventivo_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.chiudiManualmentePreventivo(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(preventivoRepository);
    }

    @Test
    void chiudiManualmentePreventivo_quandoNonEsiste_lanciaServiceException() {
        when(preventivoRepository.findById(1)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.chiudiManualmentePreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_TROVATO, exception.getError());
    }

    @Test
    void chiudiManualmentePreventivo_quandoGiaAccettato_lanciaServiceException() {
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(creaPreventivoPo(1, StatoPreventivo.ACCETTATO)));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.chiudiManualmentePreventivo(1));
        assertEquals(AppError.PREVENTIVO_NON_MODIFICABILE, exception.getError());
        verify(preventivoRepository, never()).save(any());
    }

    @Test
    void chiudiManualmentePreventivo_quandoAperto_salvaScaduto() {
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.INVIATO);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        when(preventivoRepository.save(preventivoPo)).thenReturn(preventivoPo);
        when(preventivoMapper.toDto(preventivoPo)).thenReturn(creaPreventivoRisposta(1));
        PreventivoTo risultato = preventivoService.chiudiManualmentePreventivo(1);
        assertNotNull(risultato);
        assertEquals(StatoPreventivo.SCADUTO, preventivoPo.getStatoPreventivo());
        assertNotNull(preventivoPo.getDataModificaPreventivo());
    }

    @Test
    void chiudiManualmentePreventivo_quandoSaveFallisce_lanciaServiceException() {
        PreventivoPo preventivoPo = creaPreventivoPo(1, StatoPreventivo.RICHIESTO);
        when(preventivoRepository.findById(1)).thenReturn(Optional.of(preventivoPo));
        when(preventivoRepository.save(preventivoPo)).thenThrow(new RuntimeException("DB error"));
        ServiceException exception = assertThrows(ServiceException.class, () -> preventivoService.chiudiManualmentePreventivo(1));
        assertEquals(AppError.PREVENTIVO_MODIFICA_FALLITA, exception.getError());
    }

    private PreventivoTo creaRichiestaPreventivoBase() {
        PreventivoTo preventivoTo = new PreventivoTo();
        preventivoTo.setIdUtente(3);
        preventivoTo.setTitoloPreventivo("Preventivo test");
        preventivoTo.setDescrizioneRichiestaPreventivo("Descrizione richiesta test");
        return preventivoTo;
    }

    private PreventivoTo creaModificaPreventivoBase() {
        PreventivoTo preventivoTo = new PreventivoTo();
        preventivoTo.setIdPreventivo(1);
        preventivoTo.setIdUtente(3);
        preventivoTo.setTitoloPreventivo("Preventivo modificato");
        preventivoTo.setDescrizioneRichiestaPreventivo("Descrizione modificata");
        preventivoTo.setImportoPreventivo(BigDecimal.valueOf(100));
        preventivoTo.setRispostaPreventivo("Risposta preventivo");
        return preventivoTo;
    }

    private PreventivoTo creaPreventivoRisposta(Integer idPreventivo) {
        PreventivoTo preventivoTo = new PreventivoTo();
        preventivoTo.setIdPreventivo(idPreventivo);
        return preventivoTo;
    }

    private PreventivoPo creaPreventivoPo(Integer idPreventivo, StatoPreventivo statoPreventivo) {
        PreventivoPo preventivoPo = new PreventivoPo();
        preventivoPo.setIdPreventivo(idPreventivo);
        preventivoPo.setStatoPreventivo(statoPreventivo);
        preventivoPo.setTitoloPreventivo("Preventivo test");
        preventivoPo.setDescrizioneRichiestaPreventivo("Descrizione test");
        return preventivoPo;
    }

    private UtentePo creaUtente(boolean attivo) {
        UtentePo utentePo = new UtentePo();
        utentePo.setIdUtente(3);
        utentePo.setAttivoUtente(attivo);
        return utentePo;
    }

    private ServizioPo creaServizio(boolean attivo) {
        ServizioPo servizioPo = new ServizioPo();
        servizioPo.setIdServizio(2);
        servizioPo.setAttivoServizio(attivo);
        return servizioPo;
    }
}