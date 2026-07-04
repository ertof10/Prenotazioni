package com.prenotazioni.Service;



import com.prenotazioni.Dao.PrenotazioneRepository;
import com.prenotazioni.Dao.UtenteRepository;
import com.prenotazioni.Dto.UtenteTo;
import com.prenotazioni.Enums.AppRole;
import com.prenotazioni.Error.AppError;
import com.prenotazioni.Error.ServiceException;
import com.prenotazioni.Mapper.UtenteMapper;
import com.prenotazioni.Po.UtentePo;
import com.prenotazioni.Response.EsitoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtenteServiceImpl implements UtenteService {

    private final UtenteRepository utenteRepository;
    private final UtenteMapper utenteMapper;
    private final PrenotazioneRepository prenotazioneRepository;

    public UtenteServiceImpl(UtenteRepository utenteRepository, UtenteMapper utenteMapper, PrenotazioneRepository prenotazioneRepository) {
        this.utenteRepository = utenteRepository;
        this.utenteMapper = utenteMapper;
        this.prenotazioneRepository = prenotazioneRepository;
    }

    @Override
    @Transactional
    public UtenteTo saveOrUpdateUtente(UtenteTo utenteTo) {

        String emailInserita = utenteTo.getEmailUtente().trim().toLowerCase();
        utenteTo.setEmailUtente(emailInserita);

        if (utenteTo.getIdUtente() == null) {
            if (utenteRepository.existsByEmailUtente(emailInserita)) {
                throw new ServiceException(AppError.EMAIL_GIA_PRESENTE);
            }

            UtentePo utentePo = utenteMapper.toEntity(utenteTo);
            utentePo.setRuoloUtente(AppRole.UTENTE); // IMPOSTA IL RUOLO DI DEFAULT A "UTENTE"
            utentePo = utenteRepository.save(utentePo);
            return utenteMapper.toDto(utentePo);
        }

        UtentePo esistente = utenteRepository.findById(utenteTo.getIdUtente()).orElse(null);

        if (esistente == null) {
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        if (!emailInserita.equalsIgnoreCase(esistente.getEmailUtente()) && utenteRepository.existsByEmailUtente(emailInserita)) {
            throw new ServiceException(AppError.EMAIL_GIA_PRESENTE);
        }

        esistente.setNomeUtente(utenteTo.getNomeUtente());
        esistente.setCognomeUtente(utenteTo.getCognomeUtente());
        esistente.setEmailUtente(utenteTo.getEmailUtente());
        esistente.setTelefonoUtente(utenteTo.getTelefonoUtente());

        esistente = utenteRepository.save(esistente);
        return utenteMapper.toDto(esistente);
    }

    @Override
    public UtenteTo getUtenteById(Integer idUtente) {
        UtentePo entity = utenteRepository.findById(idUtente).orElse(null);

        if (entity == null) {
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        return utenteMapper.toDto(entity);
    }

    @Override
    public List<UtenteTo> getAllUtenti() {
        return utenteRepository.findAll()
                .stream()
                .map(utenteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EsitoResponse deleteUtente(Integer idUtente) {

        if (idUtente == null || idUtente <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!utenteRepository.existsById(idUtente)) {
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        if (prenotazioneRepository.existsByUtentePo_IdUtente(idUtente)) {
            throw new ServiceException(AppError.UTENTE_NON_ELIMINABILE);
        }

        try {
            utenteRepository.deleteById(idUtente);
            return new EsitoResponse("Utente eliminato correttamente");
        } catch (Exception e) {
            throw new ServiceException(AppError.UTENTE_ELIMINAZIONE_FALLITA, e);
        }
    }
}