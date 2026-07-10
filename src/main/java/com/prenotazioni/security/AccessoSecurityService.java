package com.prenotazioni.security;

import com.prenotazioni.dao.AccountRepository;
import com.prenotazioni.dao.PrenotazioneRepository;
import com.prenotazioni.dao.PreventivoRepository;
import com.prenotazioni.enums.AppRole;
import com.prenotazioni.po.AccountPo;
import com.prenotazioni.po.PrenotazionePo;
import com.prenotazioni.po.PreventivoPo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Transactional(readOnly = true)
@Service("accessoSecurityService")
public class AccessoSecurityService {

    private final AccountRepository accountRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final PreventivoRepository preventivoRepository;

    public AccessoSecurityService(AccountRepository accountRepository,
                                  PrenotazioneRepository prenotazioneRepository,
                                  PreventivoRepository preventivoRepository) {
        this.accountRepository = accountRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.preventivoRepository = preventivoRepository;
    }

    public boolean isAdmin() {

        AccountPo accountPo = getAccountAutenticato();

        return accountPo != null && accountPo.getRuoloAccount() == AppRole.ADMIN;
    }

    public boolean isUtenteAutenticato(Integer idUtente) {

        if (idUtente == null) {
            return false;
        }

        AccountPo accountPo = getAccountAutenticato();

        return accountPo != null
                && accountPo.getRuoloAccount() == AppRole.UTENTE
                && accountPo.getUtentePo() != null
                && idUtente.equals(accountPo.getUtentePo().getIdUtente());
    }

    public boolean isCollaboratoreAutenticato(Integer idCollaboratore) {

        if (idCollaboratore == null) {
            return false;
        }

        AccountPo accountPo = getAccountAutenticato();

        return accountPo != null
                && accountPo.getRuoloAccount() == AppRole.COLLABORATORE
                && accountPo.getCollaboratorePo() != null
                && idCollaboratore.equals(accountPo.getCollaboratorePo().getIdCollaboratore());
    }

    public boolean isPrenotazioneDiUtenteAutenticato(Integer idPrenotazione) {

        if (idPrenotazione == null) {
            return false;
        }

        AccountPo accountPo = getAccountAutenticato();

        if (accountPo == null
                || accountPo.getRuoloAccount() != AppRole.UTENTE
                || accountPo.getUtentePo() == null) {
            return false;
        }

        Optional<PrenotazionePo> prenotazionePoOptional =
                prenotazioneRepository.findById(idPrenotazione);

        return prenotazionePoOptional
                .map(prenotazionePo -> prenotazionePo.getUtentePo() != null
                        && prenotazionePo.getUtentePo().getIdUtente().equals(
                        accountPo.getUtentePo().getIdUtente()
                ))
                .orElse(false);
    }

    public boolean isPrenotazioneDiCollaboratoreAutenticato(Integer idPrenotazione) {

        if (idPrenotazione == null) {
            return false;
        }

        AccountPo accountPo = getAccountAutenticato();

        if (accountPo == null
                || accountPo.getRuoloAccount() != AppRole.COLLABORATORE
                || accountPo.getCollaboratorePo() == null) {
            return false;
        }

        Optional<PrenotazionePo> prenotazionePoOptional =
                prenotazioneRepository.findById(idPrenotazione);

        return prenotazionePoOptional
                .map(prenotazionePo -> prenotazionePo.getCollaboratorePo() != null
                        && prenotazionePo.getCollaboratorePo().getIdCollaboratore().equals(
                        accountPo.getCollaboratorePo().getIdCollaboratore()
                ))
                .orElse(false);
    }

    public boolean isPreventivoDiUtenteAutenticato(Integer idPreventivo) {

        if (idPreventivo == null) {
            return false;
        }

        AccountPo accountPo = getAccountAutenticato();

        if (accountPo == null
                || accountPo.getRuoloAccount() != AppRole.UTENTE
                || accountPo.getUtentePo() == null) {
            return false;
        }

        Optional<PreventivoPo> preventivoPoOptional =
                preventivoRepository.findById(idPreventivo);

        return preventivoPoOptional
                .map(preventivoPo -> preventivoPo.getUtentePo() != null
                        && preventivoPo.getUtentePo().getIdUtente().equals(
                        accountPo.getUtentePo().getIdUtente()
                ))
                .orElse(false);
    }

    private AccountPo getAccountAutenticato() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        return accountRepository
                .findByEmailAccount(authentication.getName())
                .orElse(null);
    }
}
