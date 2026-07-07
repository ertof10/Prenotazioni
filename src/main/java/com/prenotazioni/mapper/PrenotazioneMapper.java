package com.prenotazioni.mapper;

import com.prenotazioni.dto.PrenotazioneTo;
import com.prenotazioni.po.PrenotazionePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrenotazioneMapper {

    @Mapping(source = "utentePo.idUtente", target = "idUtente")
    @Mapping(source = "servizioPo.idServizio", target = "idServizio")
    @Mapping(source = "collaboratorePo.idCollaboratore", target = "idCollaboratore")
    @Mapping(source = "collaboratorePo.nomeCollaboratore", target = "nomeCollaboratore")
    @Mapping(source = "collaboratorePo.cognomeCollaboratore", target = "cognomeCollaboratore")
    PrenotazioneTo toDto(PrenotazionePo prenotazionePo);
}