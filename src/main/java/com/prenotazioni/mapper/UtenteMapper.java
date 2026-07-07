package com.prenotazioni.mapper;

import com.prenotazioni.dto.UtenteTo;
import com.prenotazioni.po.UtentePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

    UtenteTo toDto(UtentePo utentePo);

    @Mapping(target = "ruoloUtente", ignore = true)
    UtentePo toEntity(UtenteTo utenteTo);


}