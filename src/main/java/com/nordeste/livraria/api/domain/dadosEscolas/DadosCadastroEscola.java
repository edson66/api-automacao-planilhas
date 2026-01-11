package com.nordeste.livraria.api.domain.dadosEscolas;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroEscola(
        @NotBlank
        String cnpjLimpo,
        @NotBlank
        String cnpj,
        @NotBlank
        String nome,
        @NotBlank
        String cep,
        @NotBlank
        String cidade,
        @NotBlank
        String endereco,
        String diretor
) {
    public DadosCadastroEscola(Escola escolaMock) {
        this(escolaMock.getCnpjLimpo(), escolaMock.getCnpj(), escolaMock.getNome(),
                escolaMock.getCep(), escolaMock.getCidade(), escolaMock.getEndereco(), escolaMock.getDiretor());
    }
}
