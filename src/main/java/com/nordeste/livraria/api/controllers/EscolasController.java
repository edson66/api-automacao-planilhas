package com.nordeste.livraria.api.controllers;

import com.nordeste.livraria.api.domain.dadosEscolas.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/escolas")
public class EscolasController {

    @Autowired
    private EscolaRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity addEscola(@RequestBody @Valid DadosCadastroEscola dados,
                                    UriComponentsBuilder builder){
        var escola = new Escola(dados);

        repository.save(escola);

        var uri = builder.path("/escolas/{id}").buildAndExpand(escola.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosCompletosEscola(escola));
    }

    @GetMapping
    public ResponseEntity<Page<DadosCompletosEscola>> listarEscolas(
            @PageableDefault(sort = {"id"}) Pageable pageable
            ){

        var escolas = repository.findAll(pageable).map(DadosCompletosEscola::new);

        return ResponseEntity.ok(escolas);
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarEscola(@RequestParam Long id){
        var escola = repository.findById(id);

        return ResponseEntity.ok(new DadosCompletosEscola(escola.orElseThrow(() ->
                new EntityNotFoundException("Escola não encontrada!"))));
    }

    @GetMapping("/buscar")
    public ResponseEntity buscarPorCnpj(@RequestParam("cnpj") String cnpjLimpo){
        var escola = repository.findByCnpjLimpo(cnpjLimpo)
                .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada!"));


        return ResponseEntity.ok(new DadosCompletosEscola(escola));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity atualizarDados(@PathVariable Long id,
                                         @RequestBody DadosAtualizacaoEscola dados){
        var escola = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada!"));

        escola.atualizarDados(dados);

        return ResponseEntity.ok().body(new DadosCompletosEscola(escola));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity exlcuir(@PathVariable Long id){
        var escola = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada!"));

        repository.delete(escola);

        return ResponseEntity.noContent().build();
    }
}
