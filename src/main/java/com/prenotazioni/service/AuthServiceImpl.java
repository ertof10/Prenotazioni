package com.prenotazioni.service;

import com.prenotazioni.dao.AccountRepository;
import com.prenotazioni.dao.UtenteRepository;
import com.prenotazioni.dto.AuthLoginRequest;
import com.prenotazioni.dto.AuthLoginResponse;
import com.prenotazioni.dto.AuthRegisterRequest;
import com.prenotazioni.enums.AppRole;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.po.AccountPo;
import com.prenotazioni.po.UtentePo;
import com.prenotazioni.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AccountRepository accountRepository;
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(AccountRepository accountRepository,
                           UtenteRepository utenteRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.accountRepository = accountRepository;
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthLoginResponse register(AuthRegisterRequest request) {

        String emailNormalizzata = normalizzaEmail(request.getEmail());

        log.info("Avvio registrazione cliente | email={}", emailNormalizzata);

        if (utenteRepository.existsByEmailUtente(emailNormalizzata)
                || accountRepository.existsByEmailAccount(emailNormalizzata)) {

            log.warn("Registrazione cliente bloccata: email già presente | email={}", emailNormalizzata);
            throw new ServiceException(AppError.EMAIL_GIA_PRESENTE);
        }

        try {
            UtentePo utentePo = new UtentePo();
            utentePo.setNomeUtente(request.getNomeUtente().trim());
            utentePo.setCognomeUtente(request.getCognomeUtente().trim());
            utentePo.setEmailUtente(emailNormalizzata);
            utentePo.setTelefonoUtente(normalizzaTelefono(request.getTelefonoUtente()));
            utentePo.setRuoloUtente(AppRole.UTENTE);
            utentePo.setAttivoUtente(true);

            utentePo = utenteRepository.save(utentePo);

            AccountPo accountPo = new AccountPo();
            accountPo.setEmailAccount(emailNormalizzata);
            accountPo.setPasswordAccount(passwordEncoder.encode(request.getPassword()));
            accountPo.setRuoloAccount(AppRole.UTENTE);
            accountPo.setAttivoAccount(true);
            accountPo.setUtentePo(utentePo);
            accountPo.setCollaboratorePo(null);
            accountPo.setDataCreazioneAccount(LocalDateTime.now());
            accountPo.setDataModificaAccount(null);

            accountPo = accountRepository.save(accountPo);

            String token = jwtService.generaToken(accountPo);

            log.info("Registrazione cliente completata | idAccount={} | idUtente={} | email={}",
                    accountPo.getIdAccount(),
                    utentePo.getIdUtente(),
                    emailNormalizzata);

            return creaResponse(accountPo, token);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Registrazione cliente fallita per errore tecnico | email={}", emailNormalizzata, e);
            throw new ServiceException(AppError.UTENTE_CREAZIONE_FALLITA, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthLoginResponse login(AuthLoginRequest request) {

        String emailNormalizzata = normalizzaEmail(request.getEmail());

        log.info("Avvio login account | email={}", emailNormalizzata);

        AccountPo accountPo = accountRepository.findByEmailAccount(emailNormalizzata).orElse(null);

        if (accountPo == null) {
            log.warn("Login fallito: account non trovato | email={}", emailNormalizzata);
            throw new ServiceException(AppError.CREDENZIALI_NON_VALIDE);
        }

        if (!Boolean.TRUE.equals(accountPo.getAttivoAccount())) {
            log.warn("Login fallito: account non attivo | idAccount={} | email={}",
                    accountPo.getIdAccount(),
                    emailNormalizzata);
            throw new ServiceException(AppError.ACCOUNT_NON_ATTIVO);
        }

        if (!passwordEncoder.matches(request.getPassword(), accountPo.getPasswordAccount())) {
            log.warn("Login fallito: password non valida | idAccount={} | email={}",
                    accountPo.getIdAccount(),
                    emailNormalizzata);
            throw new ServiceException(AppError.CREDENZIALI_NON_VALIDE);
        }

        String token = jwtService.generaToken(accountPo);

        log.info("Login completato | idAccount={} | email={} | ruolo={}",
                accountPo.getIdAccount(),
                emailNormalizzata,
                accountPo.getRuoloAccount());

        return creaResponse(accountPo, token);
    }

    private AuthLoginResponse creaResponse(AccountPo accountPo, String token) {

        Integer idUtente = null;
        Integer idCollaboratore = null;

        if (accountPo.getUtentePo() != null) {
            idUtente = accountPo.getUtentePo().getIdUtente();
        }

        if (accountPo.getCollaboratorePo() != null) {
            idCollaboratore = accountPo.getCollaboratorePo().getIdCollaboratore();
        }

        return new AuthLoginResponse(
                token,
                "Bearer",
                accountPo.getIdAccount(),
                idUtente,
                idCollaboratore,
                accountPo.getEmailAccount(),
                accountPo.getRuoloAccount()
        );
    }

    private String normalizzaEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String normalizzaTelefono(String telefono) {

        if (telefono == null || telefono.trim().isEmpty()) {
            return null;
        }

        return telefono.trim();
    }
}