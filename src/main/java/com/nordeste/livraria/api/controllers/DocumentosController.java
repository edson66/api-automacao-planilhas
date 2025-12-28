package com.nordeste.livraria.api.controllers;

import com.nordeste.livraria.api.automacao.GeradorArquivosService;
import com.nordeste.livraria.api.domain.dadosAutomacao.DadosEntradaAutomacao;
import com.nordeste.livraria.api.domain.dadosEscolas.EscolaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;


@RestController
@RequestMapping("/documentos")
public class DocumentosController {

    @Autowired
    private EscolaRepository repository;

    @Autowired
    private GeradorArquivosService arquivosService;

    @PostMapping("/gerar")
    @Transactional
    public ResponseEntity<ByteArrayResource> gerarDocs(
            @RequestParam("cnpj") String cnpj,
            @RequestParam("nf") String nf,
            @RequestParam("dataOrcamentos") LocalDate dataOrcamentos,
            @RequestParam(value = "dataConsolidacao", required = false) LocalDate dataConsolidacao,
            @RequestParam(value = "dataRecibo", required = false) LocalDate dataRecibo,
            @RequestParam(value = "dataNota", required = false) LocalDate dataNota,
            @RequestParam("temConsolidacao") boolean temConsolidacao,
            @RequestParam("temRecibo") boolean temRecibo,
            @RequestParam(value = "pagoPorMeioDe", required = false) String pagoPorMeioDe,
            @RequestParam("arquivoDoador")MultipartFile arquivoDoador
            ) throws IOException {

        var escola = repository.findByCnpj(cnpj)
                .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada."));

        var dto = new DadosEntradaAutomacao(escola,nf,dataOrcamentos,dataConsolidacao,
                dataRecibo,dataNota,temConsolidacao,temRecibo,
                pagoPorMeioDe != null? pagoPorMeioDe:"",arquivoDoador);

        byte[] arquivoZipFinal = arquivosService.gerarArquivos(dto);

        ByteArrayResource resource = new ByteArrayResource(arquivoZipFinal);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=documentos_gerados.zip")
                .contentLength(arquivoZipFinal.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
