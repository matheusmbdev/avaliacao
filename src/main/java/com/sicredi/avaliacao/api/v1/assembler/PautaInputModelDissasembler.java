package com.sicredi.avaliacao.api.v1.assembler;

import com.sicredi.avaliacao.api.v1.model.input.PautaInputModel;
import com.sicredi.avaliacao.domain.model.Pauta;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PautaInputModelDissasembler {

    @Autowired
    private ModelMapper modelMapper;

    public Pauta toDomainObject(PautaInputModel pautaInputModel) {
        return modelMapper.map(pautaInputModel, Pauta.class);
    }

    public List<Pauta> toCollectionModel(List<PautaInputModel> pautaInputModelList) {
        return pautaInputModelList.stream()
                .map(this::toDomainObject).collect(Collectors.toList());
    }
}
