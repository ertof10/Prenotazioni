package com.prenotazioni.dao;

import com.prenotazioni.enums.AppRole;
import com.prenotazioni.po.AccountPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountPo, Integer> {

    boolean existsByEmailAccount(String emailAccount);

    boolean existsByRuoloAccount(AppRole ruoloAccount);

    Optional<AccountPo> findByEmailAccount(String emailAccount);
}