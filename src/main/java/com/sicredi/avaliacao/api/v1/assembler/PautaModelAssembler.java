package com.sicredi.avaliacao.api.v1.assembler;

import com.sicredi.avaliacao.api.v1.model.PautaModel;
import com.sicredi.avaliacao.domain.model.Pauta;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PautaModelAssembler {

    @Autowired
    private ModelMapper modelMapper;

    public PautaModel toModel(Pauta pauta) {
        return modelMapper.map(pauta, PautaModel.class);
    }

    public List<PautaModel> toCollectionModel(List<Pauta> pautaList) {
        return pautaList.stream()
                .map(this::toModel).collect(Collectors.toList());
    }
}
