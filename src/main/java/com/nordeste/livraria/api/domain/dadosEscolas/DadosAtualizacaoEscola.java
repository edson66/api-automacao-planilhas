package com.nordeste.livraria.api.domain.dadosEscolas;

public record DadosAtualizacaoEscola(
        String cnpjLimpo,
        String cnpj,
        String nome,
        String cep,
        String cidade,
        String endereco,
        String diretor
) {
}
