package com.sicredi.avaliacao.api.v1.assembler;

import com.sicredi.avaliacao.api.v1.model.VotoModel;
import com.sicredi.avaliacao.domain.model.Voto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VotoModelAssembler {

    @Autowired
    private ModelMapper modelMapper;

    public VotoModel toModel(Voto voto) {
        return modelMapper.map(voto, VotoModel.class);
    }

    public List<VotoModel> toCollectionModel(List<Voto> votoList) {
        return votoList.stream()
                .map(this::toModel).collect(Collectors.toList());
    }
}
