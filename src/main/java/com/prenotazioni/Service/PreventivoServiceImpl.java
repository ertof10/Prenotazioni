package com.prenotazioni.Service;

import com.prenotazioni.Dao.PreventivoRepository;
import com.prenotazioni.Dao.ServizioRepository;
import com.prenotazioni.Dao.UtenteRepository;
import com.prenotazioni.Dto.PreventivoTo;
import com.prenotazioni.Enums.StatoPreventivo;
import com.prenotazioni.Error.AppError;
import com.prenotazioni.Error.ServiceException;
import com.prenotazioni.Mapper.PreventivoMapper;
import com.prenotazioni.Po.PreventivoPo;
import com.prenotazioni.Po.ServizioPo;
import com.prenotazioni.Po.UtentePo;
import com.prenotazioni.Response.EsitoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PreventivoServiceImpl implements PreventivoService {
    private final PreventivoRepository preventivoRepository;
    private final UtenteRepository utenteRepository;
    private final ServizioRepository servizioRepository;
    private final PreventivoMapper preventivoMapper;

    public PreventivoServiceImpl(PreventivoRepository preventivoRepository, UtenteRepository utenteRepository, ServizioRepository servizioRepository, PreventivoMapper preventivoMapper) {
        this.preventivoRepository = preventivoRepository;
        this.utenteRepository = utenteRepository;
        this.servizioRepository = servizioRepository;
        this.preventivoMapper = preventivoMapper;
    }

    @Override
    @Transactional
    public PreventivoTo richiediPreventivo(PreventivoTo preventivoTo) {
        if (preventivoTo == null) {
            throw new ServiceException(AppError.PREVENTIVO_NON_VALIDO);
        }
        if (preventivoTo.getIdUtente() == null || preventivoTo.getIdUtente() <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
        if (preventivoTo.getTitoloPreventivo() == null || preventivoTo.getTitoloPreventivo().trim().isEmpty() || preventivoTo.getDescrizioneRichiestaPreventivo() == null || preventivoTo.getDescrizioneRichiestaPreventivo().trim().isEmpty()) {
            throw new ServiceException(AppError.PREVENTIVO_NON_VALIDO);
        }
        UtentePo utentePo = utenteRepository.findById(preventivoTo.getIdUtente()).orElse(null);
        if (utentePo == null) {
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }
        if (!Boolean.TRUE.equals(utentePo.getAttivoUtente())) {
            throw new ServiceException(AppError.UTENTE_NON_ATTIVO);
        }
        ServizioPo servizioPo = null;
        if (preventivoTo.getIdServizio() != null) {
            if (preventivoTo.getIdServizio() <= 0) {
                throw new ServiceException(AppError.ID_NON_VALIDO);
            }
            servizioPo = servizioRepository.findById(preventivoTo.getIdServizio()).orElse(null);
            if (servizioPo == null) {
                throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
            }
            if (!Boolean.TRUE.equals(servizioPo.getAttivoServizio())) {
                throw new ServiceException(AppError.SERVIZIO_NON_ATTIVO);
            }
        }
        try {
            PreventivoPo preventivoPo = new PreventivoPo();
            preventivoPo.setUtentePo(utentePo);
            preventivoPo.setServizioPo(servizioPo);
            preventivoPo.setTitoloPreventivo(preventivoTo.getTitoloPreventivo().trim());
            preventivoPo.setDescrizioneRichiestaPreventivo(preventivoTo.getDescrizioneRichiestaPreventivo().trim());
            preventivoPo.setImportoPreventivo(null);
            preventivoPo.setRispostaPreventivo(null);
            preventivoPo.setDataScadenzaPreventivo(null);
            preventivoPo.setStatoPreventivo(StatoPreventivo.RICHIESTO);
            preventivoPo.setDataCreazionePreventivo(LocalDateTime.now());
            preventivoPo = preventivoRepository.save(preventivoPo);
            return preventivoMapper.toDto(preventivoPo);
        } catch (Exception e) {
            throw new ServiceException(AppError.PREVENTIVO_CREAZIONE_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PreventivoTo modificaPreventivo(PreventivoTo preventivoTo) {
        if (preventivoTo == null) {
            throw new ServiceException(AppError.PREVENTIVO_NON_VALIDO);
        }
        if (preventivoTo.getIdPreventivo() == null || preventivoTo.getIdPreventivo() <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
        PreventivoPo preventivoPo = preventivoRepository.findById(preventivoTo.getIdPreventivo()).orElse(null);
        if (preventivoPo == null) {
            throw new ServiceException(AppError.PREVENTIVO_NON_TROVATO);
        }
        if (preventivoPo.getStatoPreventivo() == StatoPreventivo.ACCETTATO || preventivoPo.getStatoPreventivo() == StatoPreventivo.RIFIUTATO || preventivoPo.getStatoPreventivo() == StatoPreventivo.SCADUTO) {
            throw new ServiceException(AppError.PREVENTIVO_NON_MODIFICABILE);
        }
        if (preventivoTo.getIdUtente() == null || preventivoTo.getIdUtente() <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
        UtentePo utentePo = utenteRepository.findById(preventivoTo.getIdUtente()).orElse(null);
        if (utentePo == null) {
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }
        ServizioPo servizioPo = null;
        if (preventivoTo.getIdServizio() != null) {
            if (preventivoTo.getIdServizio() <= 0) {
                throw new ServiceException(AppError.ID_NON_VALIDO);
            }
            servizioPo = servizioRepository.findById(preventivoTo.getIdServizio()).orElse(null);
            if (servizioPo == null) {
                throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
            }
        }
        if (preventivoTo.getTitoloPreventivo() == null || preventivoTo.getTitoloPreventivo().trim().isEmpty() || preventivoTo.getDescrizioneRichiestaPreventivo() == null || preventivoTo.getDescrizioneRichiestaPreventivo().trim().isEmpty()) {
            throw new ServiceException(AppError.PREVENTIVO_NON_VALIDO);
        }
        if (preventivoTo.getImportoPreventivo() != null && preventivoTo.getImportoPreventivo().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException(AppError.IMPORTO_PREVENTIVO_NON_VALIDO);
        }
        try {
            preventivoPo.setUtentePo(utentePo);
            preventivoPo.setServizioPo(servizioPo);
            preventivoPo.setTitoloPreventivo(preventivoTo.getTitoloPreventivo().trim());
            preventivoPo.setDescrizioneRichiestaPreventivo(preventivoTo.getDescrizioneRichiestaPreventivo().trim());
            preventivoPo.setImportoPreventivo(preventivoTo.getImportoPreventivo());
            preventivoPo.setRispostaPreventivo(preventivoTo.getRispostaPreventivo());
            preventivoPo.setDataScadenzaPreventivo(preventivoTo.getDataScadenzaPreventivo());
            preventivoPo.setDataModificaPreventivo(LocalDateTime.now());
            preventivoPo = preventivoRepository.save(preventivoPo);
            return preventivoMapper.toDto(preventivoPo);
        } catch (Exception e) {
            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PreventivoTo prendiInLavorazionePreventivo(Integer idPreventivo) {
        if (idPreventivo == null || idPreventivo <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
        PreventivoPo preventivoPo = preventivoRepository.findById(idPreventivo).orElse(null);
        if (preventivoPo == null) {
            throw new ServiceException(AppError.PREVENTIVO_NON_TROVATO);
        }
        if (preventivoPo.getStatoPreventivo() != StatoPreventivo.RICHIESTO) {
            throw new ServiceException(AppError.PREVENTIVO_NON_MODIFICABILE);
        }
        try {
            preventivoPo.setStatoPreventivo(StatoPreventivo.IN_LAVORAZIONE);
            preventivoPo.setDataModificaPreventivo(LocalDateTime.now());
            preventivoPo = preventivoRepository.save(preventivoPo);
            return preventivoMapper.toDto(preventivoPo);
        } catch (Exception e) {
            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PreventivoTo inviaPreventivo(Integer idPreventivo) {
        if (idPreventivo == null || idPreventivo <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
        PreventivoPo preventivoPo = preventivoRepository.findById(idPreventivo).orElse(null);
        if (preventivoPo == null) {
            throw new ServiceException(AppError.PREVENTIVO_NON_TROVATO);
        }
        if (preventivoPo.getStatoPreventivo() != StatoPreventivo.RICHIESTO && preventivoPo.getStatoPreventivo() != StatoPreventivo.IN_LAVORAZIONE) {
            throw new ServiceException(AppError.PREVENTIVO_NON_INVIABILE);
        }
        if (preventivoPo.getImportoPreventivo() == null || preventivoPo.getImportoPreventivo().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException(AppError.PREVENTIVO_NON_INVIABILE);
        }
        try {
            preventivoPo.setStatoPreventivo(StatoPreventivo.INVIATO);
            preventivoPo.setDataModificaPreventivo(LocalDateTime.now());
            preventivoPo = preventivoRepository.save(preventivoPo);
            return preventivoMapper.toDto(preventivoPo);
        } catch (Exception e) {
            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PreventivoTo accettaPreventivo(Integer idPreventivo) {
        if (idPreventivo == null || idPreventivo <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
        PreventivoPo preventivoPo = preventivoRepository.findById(idPreventivo).orElse(null);
        if (preventivoPo == null) {
            throw new ServiceException(AppError.PREVENTIVO_NON_TROVATO);
        }
        if (preventivoPo.getStatoPreventivo() != StatoPreventivo.INVIATO) {
            throw new ServiceException(AppError.PREVENTIVO_NON_ACCETTABILE);
        }
        try {
            preventivoPo.setStatoPreventivo(StatoPreventivo.ACCETTATO);
            preventivoPo.setDataModificaPreventivo(LocalDateTime.now());
            preventivoPo = preventivoRepository.save(preventivoPo);
            return preventivoMapper.toDto(preventivoPo);
        } catch (Exception e) {
            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PreventivoTo rifiutaPreventivo(Integer idPreventivo) {
        if (idPreventivo == null || idPreventivo <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
        PreventivoPo preventivoPo = preventivoRepository.findById(idPreventivo).orElse(null);
        if (preventivoPo == null) {
            throw new ServiceException(AppError.PREVENTIVO_NON_TROVATO);
        }
        if (preventivoPo.getStatoPreventivo() != StatoPreventivo.INVIATO) {
            throw new ServiceException(AppError.PREVENTIVO_NON_RIFIUTABILE);
        }
        try {
            preventivoPo.setStatoPreventivo(StatoPreventivo.RIFIUTATO);
            preventivoPo.setDataModificaPreventivo(LocalDateTime.now());
            preventivoPo = preventivoRepository.save(preventivoPo);
            return preventivoMapper.toDto(preventivoPo);
        } catch (Exception e) {
            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public PreventivoTo getPreventivoById(Integer idPreventivo) {
        if (idPreventivo == null || idPreventivo <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
        PreventivoPo preventivoPo = preventivoRepository.findById(idPreventivo).orElse(null);
        if (preventivoPo == null) {
            throw new ServiceException(AppError.PREVENTIVO_NON_TROVATO);
        }
        return preventivoMapper.toDto(preventivoPo);
    }

    @Override
    public List<PreventivoTo> getAllPreventivi() {
        return preventivoRepository.findAll().stream().map(preventivoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PreventivoTo> getPreventiviByUtente(Integer idUtente) {
        if (idUtente == null || idUtente <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
        if (!utenteRepository.existsById(idUtente)) {
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }
        return preventivoRepository.findByUtentePo_IdUtente(idUtente).stream().map(preventivoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PreventivoTo> getPreventiviByStato(String statoPreventivo) {
        StatoPreventivo stato;
        try {
            stato = StatoPreventivo.valueOf(statoPreventivo.toUpperCase());
        } catch (Exception e) {
            throw new ServiceException(AppError.STATO_PREVENTIVO_NON_VALIDO, e);
        }
        return preventivoRepository.findByStatoPreventivo(stato).stream().map(preventivoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EsitoResponse deletePreventivo(Integer idPreventivo) {
        if (idPreventivo == null || idPreventivo <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
        if (!preventivoRepository.existsById(idPreventivo)) {
            throw new ServiceException(AppError.PREVENTIVO_NON_TROVATO);
        }
        try {
            preventivoRepository.deleteById(idPreventivo);
            return new EsitoResponse("Preventivo eliminato correttamente");
        } catch (Exception e) {
            throw new ServiceException(AppError.PREVENTIVO_ELIMINAZIONE_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PreventivoTo chiudiManualmentePreventivo(Integer idPreventivo) {

        if (idPreventivo == null || idPreventivo <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PreventivoPo preventivoPo = preventivoRepository
                .findById(idPreventivo)
                .orElse(null);

        if (preventivoPo == null) {
            throw new ServiceException(AppError.PREVENTIVO_NON_TROVATO);
        }

        if (preventivoPo.getStatoPreventivo() == StatoPreventivo.ACCETTATO
                || preventivoPo.getStatoPreventivo() == StatoPreventivo.RIFIUTATO
                || preventivoPo.getStatoPreventivo() == StatoPreventivo.SCADUTO) {
            throw new ServiceException(AppError.PREVENTIVO_NON_MODIFICABILE);
        }

        try {
            preventivoPo.setStatoPreventivo(StatoPreventivo.SCADUTO);
            preventivoPo.setDataModificaPreventivo(LocalDateTime.now());

            preventivoPo = preventivoRepository.save(preventivoPo);

            return preventivoMapper.toDto(preventivoPo);

        } catch (Exception e) {
            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }
}