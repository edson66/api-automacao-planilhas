package com.nordeste.livraria.api.domain.dadosAutomacao;

import com.nordeste.livraria.api.domain.dadosEscolas.Escola;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record DadosEntradaAutomacao(
        Escola escola,
        String nf,
        LocalDate dataOrcamentos,
        LocalDate dataConsolidacao,
        LocalDate dataRecibo,
        LocalDate dataNota,
        boolean temConsolidacao,
        boolean temRecibo,
        String pagoPorMeioDe,
        MultipartFile arquivoDoador
) {


}
