package com.sicredi.avaliacao.api.v1.assembler;

import com.sicredi.avaliacao.api.v1.model.VotoModel;
import com.sicredi.avaliacao.domain.model.Voto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VotoModelDissasembler {

    @Autowired
    private ModelMapper modelMapper;

    public Voto toDomainObject(VotoModel votoModel) {
        return modelMapper.map(votoModel, Voto.class);
    }

    public List<Voto> toCollectionModel(List<VotoModel> votoModelList) {
        return votoModelList.stream()
                .map(this::toDomainObject).collect(Collectors.toList());
    }
}
