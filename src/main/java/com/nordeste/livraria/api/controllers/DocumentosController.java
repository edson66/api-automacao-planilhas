package com.nordeste.livraria.api.controllers;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentos")
public class DocumentosController {

    @PostMapping
    @Transactional
    public ResponseEntity gerarDocs(){
        return ResponseEntity.ok().build();
    }
}
