# Livraria DocGen API

![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Status](https://img.shields.io/badge/Status-Production-brightgreen?style=for-the-badge)

##  Sobre o Projeto

API RESTful desenvolvida para automatizar a geração de orçamentos, recibos e documentos fiscais no setor de varejo.

O sistema resolve o problema de **precificação complexa**: a partir de uma lista base de itens, a API gera automaticamente múltiplas versões do orçamento ("Paper", "Grafite", "NCE") aplicando **regras de negócio dinâmicas** para cálculo de margens, impostos e custos operacionais variáveis.

Isso substitui um processo manual propenso a falhas por um motor de cálculo robusto que entrega todos os cenários prontos em segundos.

> **Nota:** Este repositório contém a **versão 2.0 (Arquitetura Web)** da solução, migrada de scripts locais para uma API escalável com Spring Boot.

---

##  Funcionalidades Principais

- **Motor de Precificação Dinâmica:**
    - O sistema recebe o custo base e aplica algoritmos de *markup* variáveis para gerar diferentes tabelas de preço (Tabela A, Tabela B, etc.), simulando diferentes cargas tributárias ou margens de lucro.
    - Regras de arredondamento inteligentes para manter os valores comerciais.
- **Geração de Planilhas (Excel/Apache POI):**
    - Criação dinâmica de arquivos `.xlsx` formatados para cada cenário de preço.
    - Preenchimento automático de cabeçalhos e totais.
- **Automação de Documentos (Word/poi-tl):**
    - Geração de Recibos e Termos de Consolidação baseados em templates.
    - Escrita de valores por extenso automática.
- **Processamento In-Memory:**
    - Manipulação de arquivos 100% em memória RAM (Streams), garantindo alta performance e segurança sem depender de I/O de disco.
    - Entrega compactada em formato **ZIP**.

---

##  Tech Stack

- **Linguagem:** Java 21+
- **Framework:** Spring Boot 3 (Web, Data JPA)
- **Core de Automação:**
    - *Apache POI* (Manipulação avançada de Excel)
    - *poi-tl* (Engine de templates Word)
- **Banco de Dados:** H2 Database
- **Outros:** Flyway (Migrations), Lombok,Swagger.

---

##  Destaques Técnicos

### Algoritmo de Precificação ("RegrasDeNegocio")
O maior desafio deste projeto não foi apenas gerar arquivos, mas implementar a lógica de negócio que define os preços finais. O `DadosService` aplica regras condicionais baseadas no valor unitário do item para definir a margem de contribuição correta para cada arquivo de saída, garantindo que os orçamentos gerados atendam aos requisitos financeiros da empresa automaticamente.

### Arquitetura Limpa
O projeto segue princípios de SOLID, separando claramente as responsabilidades:
- **Controllers:** Apenas recebem requisições e devolvem respostas.
- **Services:** Contêm a lógica pesada de geração de arquivos.
- **DTOs (Records):** Garantem a integridade dos dados trafegados.

---

## Como Rodar

### Pré-requisitos
- Java 21 instalado.

### Passos
1. Clone o repositório.
2. Execute o projeto via Maven ou sua IDE de preferência.
3. Acesse `http://localhost:8080`.
4. Utilize o formulário na interface web para enviar uma planilha base e gerar o pacote de documentos.

---

##  Impacto

A implementação desta API permitiu:
- Redução de **90% no tempo operacional** de geração de propostas.
- Eliminação de erros de cálculo humano na aplicação das margens de lucro.
- Padronização total dos documentos enviados a clientes e parceiros.

---

## Licença

Desenvolvido para uso profissional. Direitos reservados.