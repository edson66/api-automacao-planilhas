package com.nordeste.livraria.api.domain.dadosEscolas;

import jakarta.validation.constraints.NotBlank;

public record DadosCompletosEscola(
        Long id,
        String cnpjLimpo,
        String cnpj,
        String nome,
        String cep,
        String cidade,
        String endereco,
        String diretor
) {
    public DadosCompletosEscola(Escola escola) {
        this(escola.getId(), escola.getCnpjLimpo(), escola.getCnpj(), escola.getNome(),
                escola.getCep(), escola.getCidade(), escola.getEndereco(), escola.getDiretor());
    }
}
