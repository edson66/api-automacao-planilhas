package com.nordeste.livraria.api.domain.dadosEscolas;

import jakarta.persistence.*;

import jakarta.validation.Valid;
import lombok.*;


@Entity
@Table(name = "escolas")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Escola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cnpjLimpo;
    private String cnpj;
    private String nome;
    private String cep;
    private String cidade;
    private String endereco;
    private String diretor;

    public Escola(DadosCadastroEscola dados) {
        this.cnpjLimpo = dados.cnpjLimpo();
        this.cnpj = dados.cnpj();
        this.nome = dados.nome();
        this.cep = dados.cep();
        this.cidade = dados.cidade();
        this.endereco = dados.endereco();
        this.diretor = dados.diretor();
    }

    public void atualizarDados(DadosAtualizacaoEscola dados) {
        if (dados.cnpjLimpo() != null){
            this.cnpjLimpo = dados.cnpjLimpo();
        }
        if (dados.cnpj() != null){
            this.cnpj = dados.cnpj();
        }
        if (dados.nome() != null){
            this.nome = dados.nome();
        }
        if (dados.cep() != null){
            this.cep = dados.cep();
        }
        if (dados.cidade() != null){
            this.cidade = dados.cidade();
        }
        if (dados.endereco() != null){
            this.endereco = dados.endereco();
        }
        if (dados.diretor() != null){
            this.diretor = dados.diretor();
        }
    }
}
