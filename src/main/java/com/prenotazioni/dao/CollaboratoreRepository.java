package com.prenotazioni.dao;

import com.prenotazioni.po.CollaboratorePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface CollaboratoreRepository extends JpaRepository<CollaboratorePo, Integer> {

    boolean existsByEmailCollaboratore(String emailCollaboratore);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT collaboratorePo " +
            "FROM CollaboratorePo collaboratorePo " +
            "WHERE collaboratorePo.idCollaboratore = :idCollaboratore")
    Optional<CollaboratorePo> findByIdForUpdate(
            @Param("idCollaboratore") Integer idCollaboratore
    );
}