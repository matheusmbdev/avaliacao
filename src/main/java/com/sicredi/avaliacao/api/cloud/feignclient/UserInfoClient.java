package com.sicredi.avaliacao.api.cloud.feignclient;

import com.sicredi.avaliacao.api.v1.model.AssociadoCpf;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "${user-info.url}", name = "user-info")
public interface UserInfoClient {

    @GetMapping("/users/{cpf}")
    AssociadoCpf checarCpf(@PathVariable String cpf);
}
