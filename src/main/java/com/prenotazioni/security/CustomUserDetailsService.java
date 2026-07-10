package com.prenotazioni.security;

import com.prenotazioni.dao.AccountRepository;
import com.prenotazioni.po.AccountPo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        AccountPo accountPo = accountRepository.findByEmailAccount(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account non trovato"));

        return new User(
                accountPo.getEmailAccount(),
                accountPo.getPasswordAccount(),
                Boolean.TRUE.equals(accountPo.getAttivoAccount()),
                true,
                true,
                true,
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + accountPo.getRuoloAccount().name())
                )
        );
    }
}
