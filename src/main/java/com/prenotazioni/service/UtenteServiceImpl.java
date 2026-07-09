package com.prenotazioni.service;

import com.prenotazioni.dao.PrenotazioneRepository;
import com.prenotazioni.dao.PreventivoRepository;
import com.prenotazioni.dao.UtenteRepository;
import com.prenotazioni.dto.UtenteTo;
import com.prenotazioni.enums.AppRole;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.UtenteMapper;
import com.prenotazioni.po.UtentePo;
import com.prenotazioni.response.EsitoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtenteServiceImpl implements UtenteService {

    private static final Logger log = LoggerFactory.getLogger(UtenteServiceImpl.class);

    private final UtenteRepository utenteRepository;
    private final UtenteMapper utenteMapper;
    private final PrenotazioneRepository prenotazioneRepository;
    private final PreventivoRepository preventivoRepository;

    public UtenteServiceImpl(UtenteRepository utenteRepository,
                             UtenteMapper utenteMapper,
                             PrenotazioneRepository prenotazioneRepository,
                             PreventivoRepository preventivoRepository) {
        this.utenteRepository = utenteRepository;
        this.utenteMapper = utenteMapper;
        this.prenotazioneRepository = prenotazioneRepository;
        this.preventivoRepository = preventivoRepository;
    }

    @Override
    @Transactional
    public UtenteTo saveOrUpdateUtente(UtenteTo utenteTo) {

        String emailInserita = utenteTo.getEmailUtente().trim().toLowerCase();
        utenteTo.setEmailUtente(emailInserita);

        if (utenteTo.getIdUtente() == null) {

            log.info("Avvio creazione utente | email={}", emailInserita);

            if (utenteRepository.existsByEmailUtente(emailInserita)) {
                log.warn("Creazione utente bloccata: email già presente | email={}", emailInserita);
                throw new ServiceException(AppError.EMAIL_GIA_PRESENTE);
            }

            try {
                UtentePo utentePo = utenteMapper.toEntity(utenteTo);
                utentePo.setRuoloUtente(AppRole.UTENTE);
                utentePo.setAttivoUtente(true);

                utentePo = utenteRepository.save(utentePo);

                log.info("Creazione utente completata | idUtente={} | email={}",
                        utentePo.getIdUtente(),
                        utentePo.getEmailUtente());

                return utenteMapper.toDto(utentePo);

            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                log.error("Creazione utente fallita per errore tecnico | email={}", emailInserita, e);
                throw new ServiceException(AppError.UTENTE_CREAZIONE_FALLITA, e);
            }
        }

        log.info("Avvio modifica utente | idUtente={} | email={}",
                utenteTo.getIdUtente(),
                emailInserita);

        UtentePo esistente = utenteRepository.findById(utenteTo.getIdUtente()).orElse(null);

        if (esistente == null) {
            log.warn("Modifica utente bloccata: utente non trovato | idUtente={}", utenteTo.getIdUtente());
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        if (!emailInserita.equalsIgnoreCase(esistente.getEmailUtente())
                && utenteRepository.existsByEmailUtente(emailInserita)) {

            log.warn("Modifica utente bloccata: nuova email già presente | idUtente={} | email={}",
                    utenteTo.getIdUtente(),
                    emailInserita);

            throw new ServiceException(AppError.EMAIL_GIA_PRESENTE);
        }

        try {
            esistente.setNomeUtente(utenteTo.getNomeUtente());
            esistente.setCognomeUtente(utenteTo.getCognomeUtente());
            esistente.setEmailUtente(utenteTo.getEmailUtente());
            esistente.setTelefonoUtente(utenteTo.getTelefonoUtente());

            esistente = utenteRepository.save(esistente);

            log.info("Modifica utente completata | idUtente={} | email={}",
                    esistente.getIdUtente(),
                    esistente.getEmailUtente());

            return utenteMapper.toDto(esistente);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Modifica utente fallita per errore tecnico | idUtente={}",
                    utenteTo.getIdUtente(),
                    e);

            throw new ServiceException(AppError.UTENTE_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public UtenteTo getUtenteById(Integer idUtente) {

        log.info("Ricerca utente per id | idUtente={}", idUtente);

        UtentePo entity = utenteRepository.findById(idUtente).orElse(null);

        if (entity == null) {
            log.warn("Ricerca utente fallita: utente non trovato | idUtente={}", idUtente);
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        log.info("Ricerca utente completata | idUtente={}", idUtente);

        return utenteMapper.toDto(entity);
    }

    @Override
    public Page<UtenteTo> getAllUtenti(Pageable pageable) {

        log.info("Ricerca utenti paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<UtenteTo> risultato = utenteRepository.findAll(pageable)
                .map(utenteMapper::toDto);

        log.info("Ricerca utenti completata | elementiPagina={} | totaleElementi={} | totalePagine={}",
                risultato.getNumberOfElements(),
                risultato.getTotalElements(),
                risultato.getTotalPages());

        return risultato;
    }

    @Override
    @Transactional
    public EsitoResponse deleteUtente(Integer idUtente) {

        log.info("Avvio eliminazione utente | idUtente={}", idUtente);

        if (idUtente == null || idUtente <= 0) {
            log.warn("Eliminazione utente bloccata: id non valido | idUtente={}", idUtente);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!utenteRepository.existsById(idUtente)) {
            log.warn("Eliminazione utente bloccata: utente non trovato | idUtente={}", idUtente);
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        if (prenotazioneRepository.existsByUtentePo_IdUtente(idUtente)) {
            log.warn("Eliminazione utente bloccata: prenotazioni associate presenti | idUtente={}", idUtente);
            throw new ServiceException(AppError.UTENTE_NON_ELIMINABILE);
        }

        if (preventivoRepository.existsByUtentePo_IdUtente(idUtente)) {
            log.warn("Eliminazione utente bloccata: preventivi associati presenti | idUtente={}", idUtente);
            throw new ServiceException(AppError.UTENTE_CON_PREVENTIVI_ASSOCIATI);
        }

        try {
            utenteRepository.deleteById(idUtente);

            log.info("Eliminazione utente completata | idUtente={}", idUtente);

            return new EsitoResponse("Utente eliminato correttamente");

        } catch (Exception e) {
            log.error("Eliminazione utente fallita per errore tecnico | idUtente={}", idUtente, e);
            throw new ServiceException(AppError.UTENTE_ELIMINAZIONE_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public UtenteTo disattivaUtente(Integer idUtente) {

        log.info("Avvio disattivazione utente | idUtente={}", idUtente);

        if (idUtente == null || idUtente <= 0) {
            log.warn("Disattivazione utente bloccata: id non valido | idUtente={}", idUtente);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        UtentePo utentePo = utenteRepository.findById(idUtente).orElse(null);

        if (utentePo == null) {
            log.warn("Disattivazione utente bloccata: utente non trovato | idUtente={}", idUtente);
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        utentePo.setAttivoUtente(false);

        utentePo = utenteRepository.save(utentePo);

        log.info("Disattivazione utente completata | idUtente={}", idUtente);

        return utenteMapper.toDto(utentePo);
    }

    @Override
    @Transactional
    public UtenteTo riattivaUtente(Integer idUtente) {

        log.info("Avvio riattivazione utente | idUtente={}", idUtente);

        if (idUtente == null || idUtente <= 0) {
            log.warn("Riattivazione utente bloccata: id non valido | idUtente={}", idUtente);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        UtentePo utentePo = utenteRepository.findById(idUtente).orElse(null);

        if (utentePo == null) {
            log.warn("Riattivazione utente bloccata: utente non trovato | idUtente={}", idUtente);
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        utentePo.setAttivoUtente(true);

        utentePo = utenteRepository.save(utentePo);

        log.info("Riattivazione utente completata | idUtente={}", idUtente);

        return utenteMapper.toDto(utentePo);
    }
}