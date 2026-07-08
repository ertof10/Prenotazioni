package com.prenotazioni.dao;

import com.prenotazioni.po.UtentePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<UtentePo, Integer> {

    boolean existsByEmailUtente(String emailInserita);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT utentePo " +
            "FROM UtentePo utentePo " +
            "WHERE utentePo.idUtente = :idUtente")
    Optional<UtentePo> findByIdForUpdate(
            @Param("idUtente") Integer idUtente
    );
}

