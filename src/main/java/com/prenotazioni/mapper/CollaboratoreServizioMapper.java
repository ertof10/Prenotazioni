package com.prenotazioni.mapper;

import com.prenotazioni.dto.CollaboratoreServizioTo;
import com.prenotazioni.po.CollaboratoreServizioPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CollaboratoreServizioMapper {

    @Mapping(source = "collaboratorePo.idCollaboratore", target = "idCollaboratore")
    @Mapping(source = "servizioPo.idServizio", target = "idServizio")
    CollaboratoreServizioTo toDto(CollaboratoreServizioPo collaboratoreServizioPo);
}