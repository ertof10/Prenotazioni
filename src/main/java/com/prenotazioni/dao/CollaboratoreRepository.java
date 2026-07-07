package com.prenotazioni.dao;

import com.prenotazioni.po.CollaboratorePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollaboratoreRepository extends JpaRepository<CollaboratorePo, Integer> {

    boolean existsByEmailCollaboratore(String emailCollaboratore);
}