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
}
