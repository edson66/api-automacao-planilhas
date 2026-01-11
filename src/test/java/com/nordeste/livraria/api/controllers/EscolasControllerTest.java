package com.nordeste.livraria.api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nordeste.livraria.api.domain.dadosEscolas.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EscolasController.class)
@WithMockUser
class EscolasControllerTest {

    @MockitoBean
    private EscolaRepository repository;

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Deve criar uma escola nova com sucesso")
    void addEscola() throws Exception {

        var escolaMock = new Escola();
        escolaMock.setNome("teste");
        escolaMock.setCep("teste");
        escolaMock.setCnpj("teste");
        escolaMock.setCidade("teste");
        escolaMock.setCnpjLimpo("teste");
        escolaMock.setEndereco("teste");

        var escolaDto = new DadosCadastroEscola(escolaMock);

        var json = mapper.writeValueAsString(escolaDto);

        var response = mvc.perform(post("/escolas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(csrf()));

        response.andExpect(jsonPath("$.nome").value("teste"));
        response.andExpect(status().isCreated());

        verify(repository,times(1)).save(any(Escola.class));
    }

    @Test
    @DisplayName("Deve dar erro de validação")
    void addEscolacaso2() throws Exception {

        var escolaMock = new Escola();
        escolaMock.setNome("teste");
        escolaMock.setCep("teste");
        escolaMock.setCnpj(null);
        escolaMock.setCidade("teste");
        escolaMock.setCnpjLimpo("teste");
        escolaMock.setEndereco("teste");

        var escolaDto = new DadosCadastroEscola(escolaMock);

        var json = mapper.writeValueAsString(escolaDto);

        var response = mvc.perform(post("/escolas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(csrf()));

        response.andExpect(status().isBadRequest());

        verify(repository,never()).save(any(Escola.class));
    }

    @Test
    @DisplayName("deve retornar lista das escolas")
    void buscarEscola() throws Exception {

        var escolaMock = new Escola();
        escolaMock.setId(1L);
        escolaMock.setNome("teste");
        escolaMock.setCep("teste");
        escolaMock.setCnpj("teste");
        escolaMock.setCidade("teste");
        escolaMock.setCnpjLimpo("teste");
        escolaMock.setEndereco("teste");

        when(repository.findById(1L)).thenReturn(Optional.of(escolaMock));


        var response = mvc.perform(get("/escolas/1")
                .with(csrf()));

        response.andExpect(jsonPath("$.nome").value("teste"));
        response.andExpect(status().isOk());

        verify(repository,times(1)).findById(any(Long.class));

    }

    @Test
    void buscarPorCnpj() throws Exception {

        var escolaMock = new Escola();
        escolaMock.setId(1L);
        escolaMock.setNome("teste");
        escolaMock.setCep("teste");
        escolaMock.setCnpj("teste");
        escolaMock.setCidade("teste");
        escolaMock.setCnpjLimpo("teste");
        escolaMock.setEndereco("teste");

        when(repository.findByCnpjLimpo("teste")).thenReturn(Optional.of(escolaMock));


        var response = mvc.perform(get("/escolas/buscar?cnpj=teste")
                .with(csrf()));

        response.andExpect(jsonPath("$.nome").value("teste"));
        response.andExpect(status().isOk());

        verify(repository,times(1)).findByCnpjLimpo(any(String.class));
    }

    @Test
    void atualizarDados() throws Exception {

        var escolaMock = new Escola();
        escolaMock.setNome("teste");
        escolaMock.setCep("teste");
        escolaMock.setCnpj("teste");
        escolaMock.setCidade("teste");
        escolaMock.setCnpjLimpo("teste");
        escolaMock.setEndereco("teste");


        when(repository.findById(any(Long.class))).thenReturn(Optional.of(escolaMock));

        var escolaDto = new DadosAtualizacaoEscola(null,null,"atualizei",null,null,null,null);

        var json = mapper.writeValueAsString(escolaDto);

        var response = mvc.perform(put("/escolas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(csrf()));

        response.andExpect(jsonPath("$.nome").value("atualizei"));
        response.andExpect(status().isOk());

    }

    @Test
    void exlcuir() throws Exception {
        var escolaMock = new Escola();
        escolaMock.setNome("teste");
        escolaMock.setCep("teste");
        escolaMock.setCnpj("teste");
        escolaMock.setCidade("teste");
        escolaMock.setCnpjLimpo("teste");
        escolaMock.setEndereco("teste");


        when(repository.findById(any(Long.class))).thenReturn(Optional.of(escolaMock));


        var response = mvc.perform(delete("/escolas/1")
                .with(csrf()));

        response.andExpect(status().isNoContent());
    }
}