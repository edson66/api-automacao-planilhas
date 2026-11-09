package com.nordeste.livraria.api.domain.dadosEscolas;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EscolaRepository extends JpaRepository<Escola,Long> {

    Optional<Escola> findByCnpjLimpo(String cnpjLimpo);

    List<Escola> findTop10ByNomeContainingIgnoreCaseOrderByNome(String nome);
}
