package com.prenotazioni.Mapper;

import com.prenotazioni.Dto.UtenteTo;
import com.prenotazioni.Po.UtentePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

    @Mapping(source = "attivoUtente", target = "attivoUtente")
    UtenteTo toDto(UtentePo utentePo);

    @Mapping(source = "attivoUtente", target = "attivoUtente")
    UtentePo toEntity(UtenteTo utenteTo);

    List<UtenteTo> toDtoList(List<UtentePo> utentiPo);
}