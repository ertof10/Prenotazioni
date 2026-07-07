package com.prenotazioni.mapper;

import com.prenotazioni.dto.PreventivoTo;
import com.prenotazioni.po.PreventivoPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PreventivoMapper {
    @Mapping(source = "utentePo.idUtente", target = "idUtente")
    @Mapping(source = "utentePo.nomeUtente", target = "nomeUtente")
    @Mapping(source = "utentePo.cognomeUtente", target = "cognomeUtente")
    @Mapping(source = "servizioPo.idServizio", target = "idServizio")
    @Mapping(source = "servizioPo.nomeServizio", target = "nomeServizio")
    PreventivoTo toDto(PreventivoPo preventivoPo);

    List<PreventivoTo> toDtoList(List<PreventivoPo> preventiviPo);
}