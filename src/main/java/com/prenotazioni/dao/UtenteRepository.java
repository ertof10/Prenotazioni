package com.prenotazioni.dao;


import com.prenotazioni.po.UtentePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtenteRepository extends JpaRepository<UtentePo, Integer> {

    boolean existsByEmailUtente(String emailInserita);


}