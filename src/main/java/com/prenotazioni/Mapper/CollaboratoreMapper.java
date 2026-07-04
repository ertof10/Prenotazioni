package com.prenotazioni.Mapper;

import com.prenotazioni.Dto.CollaboratoreTo;
import com.prenotazioni.Po.CollaboratorePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CollaboratoreMapper {

    CollaboratoreTo toDto(CollaboratorePo collaboratorePo);

    CollaboratorePo toEntity(CollaboratoreTo collaboratoreTo);
}