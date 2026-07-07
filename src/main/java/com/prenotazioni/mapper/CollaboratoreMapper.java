package com.prenotazioni.mapper;

import com.prenotazioni.dto.CollaboratoreTo;
import com.prenotazioni.po.CollaboratorePo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CollaboratoreMapper {

    CollaboratoreTo toDto(CollaboratorePo collaboratorePo);

    CollaboratorePo toEntity(CollaboratoreTo collaboratoreTo);
}