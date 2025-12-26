create table escolas(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cnpj_limpo VARCHAR(14) NOT NULL UNIQUE,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    cep VARCHAR(12) NOT NULL,
    cidade VARCHAR(120) NOT NULL,
    endereco VARCHAR(120) NOT NULL,
    diretor VARCHAR(255)
)