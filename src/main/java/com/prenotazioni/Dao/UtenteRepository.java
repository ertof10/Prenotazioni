package com.prenotazioni.Dao;


import com.prenotazioni.Po.UtentePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtenteRepository extends JpaRepository<UtentePo, Integer> {

    boolean existsByEmailUtente(String emailInserita);


}