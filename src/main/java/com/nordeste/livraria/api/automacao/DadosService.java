package com.nordeste.livraria.api.automacao;

import com.nordeste.livraria.api.domain.dadosAutomacao.DadosEntradaAutomacao;
import com.nordeste.livraria.api.domain.dadosEscolas.Escola;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class DadosService {

    @Autowired
    private OrcamentosService orcamentosService;

    public List<Map<String,Object>> lerArquivoDoador(MultipartFile arquivoDoador) {
        List<Map<String,Object>> dados = new ArrayList<>();

        try(Workbook workbook = new XSSFWorkbook(arquivoDoador.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(9);

            int linhaAtual = 2;

            while (true){
                Row row = sheet.getRow(linhaAtual);

                if (row == null || isCellEmpty(row.getCell(1))){
                    break;
                }

                Map<String,Object> itemDaLinha = new HashMap<>();

                itemDaLinha.put("ITEM",getCellValue(row.getCell(1)));

                itemDaLinha.put("UN",getCellValue(row.getCell(5)));

                if (row.getCell(6) != null && row.getCell(6).getCellType() == CellType.NUMERIC) {
                    itemDaLinha.put("QT", (int) row.getCell(6).getNumericCellValue());
                } else {
                    itemDaLinha.put("QT", 0);
                }

                if (row.getCell(7) != null && row.getCell(7).getCellType() == CellType.NUMERIC) {
                    itemDaLinha.put("VALOR", row.getCell(7).getNumericCellValue());
                } else {
                    itemDaLinha.put("VALOR", 0.0);
                }

                dados.add(itemDaLinha);
                linhaAtual++;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dados;
    }

    public Map<String,String> lerDadosEscolas(Escola escola){

        Map<String,String> dadosEscola = new HashMap<>();


        dadosEscola.put("<NOME>", escola.getNome());
        dadosEscola.put("<CNPJ>", escola.getCnpj());
        dadosEscola.put("<CEP>", escola.getCep());
        dadosEscola.put("<CIDADE>", escola.getCidade());
        dadosEscola.put("<ENDEREÇO>", escola.getEndereco());
        dadosEscola.put("DIRETOR", escola.getDiretor());


        return dadosEscola;
    }

    public Map<String,String> lerDadosAdicionais(Map<String,String> dadosEscolas,
                                                        DadosEntradaAutomacao dadosEntrada){

        String[] meses = {
                "JANEIRO", "FEVEREIRO", "MARÇO", "ABRIL", "MAIO", "JUNHO",
                "JULHO", "AGOSTO", "SETEMBRO", "OUTUBRO", "NOVEMBRO", "DEZEMBRO"
        };

        dadosEscolas.put("NF",dadosEntrada.nf());

        String diaOrcamentos = String.valueOf(dadosEntrada.dataOrcamentos().getDayOfMonth());
        dadosEscolas.put("DIA_ORCAMENTOS",diaOrcamentos);

        dadosEscolas.put("MES_ORCAMENTOS", String.valueOf(dadosEntrada.dataOrcamentos().getMonthValue()));
        int mesOrcamentos = dadosEntrada.dataOrcamentos().getMonthValue();

        String anoOrcamentos = String.valueOf(dadosEntrada.dataOrcamentos().getYear());
        dadosEscolas.put("ANO_ORCAMENTOS",anoOrcamentos);

        String dataOrcamentos = diaOrcamentos + " DE " + meses[mesOrcamentos-1] + " DE " + anoOrcamentos;
        dadosEscolas.put("<DATA>",dataOrcamentos);



        if (dadosEntrada.temConsolidacao()){

            dadosEscolas.put("TEM_CONSOLIDACAO","S");

            String diaConsolidacao = String.valueOf(dadosEntrada.dataConsolidacao().getDayOfMonth());
            dadosEscolas.put("DIA_CONSOLIDACAO",diaConsolidacao);

            int mesConsolidacao = dadosEntrada.dataConsolidacao().getMonthValue();
            dadosEscolas.put("MES_CONSOLIDACAO", String.valueOf(mesConsolidacao));

            String anoConsolidacao = String.valueOf(dadosEntrada.dataConsolidacao().getYear());
            dadosEscolas.put("ANO_CONSOLIDACAO",anoConsolidacao);

            String dataConsolidacao = diaConsolidacao + " de " +
                    orcamentosService.formatarTextoTitle(meses[mesConsolidacao-1]) +
                    " de " + anoConsolidacao;
            dadosEscolas.put("DATA_CONS",dataConsolidacao);


        }else {
            dadosEscolas.put("TEM_CONSOLIDACAO","N");
        }

        if (dadosEntrada.temRecibo()){

            dadosEscolas.put("TEM_RECIBO","S");

            String diaRecibo = String.valueOf(dadosEntrada.dataRecibo().getDayOfMonth());
            dadosEscolas.put("DIA_R",diaRecibo);

            int mesRecibo = dadosEntrada.dataRecibo().getMonthValue();
            dadosEscolas.put("MES_R", String.valueOf(mesRecibo));
            dadosEscolas.put("MES_R_EXTENSO",meses[mesRecibo -1]);

            String anoRecibo = String.valueOf(dadosEntrada.dataRecibo().getYear());
            dadosEscolas.put("ANO_R",anoRecibo);

            if (dadosEntrada.dataNota() == null && dadosEntrada.dataRecibo() != null) {
                dadosEscolas.put("DIA_N",diaRecibo);
                dadosEscolas.put("MES_N",String.valueOf(mesRecibo));
                dadosEscolas.put("ANO_N",anoRecibo);
            }else {
                String diaNota = String.valueOf(dadosEntrada.dataNota().getDayOfMonth());
                dadosEscolas.put("DIA_N",diaNota);

                String mesNota = String.valueOf(dadosEntrada.dataNota().getMonthValue());
                dadosEscolas.put("MES_N",mesNota);

                String anoNota = String.valueOf(dadosEntrada.dataNota().getYear());
                dadosEscolas.put("ANO_N",anoNota);
            }

            dadosEscolas.put("MEIO",dadosEntrada.pagoPorMeioDe().toUpperCase());

        }else {
            dadosEscolas.put("TEM_RECIBO","N");
        }

        return dadosEscolas;
    }

    public void aplicarRegrasDeNegocio(List<Map<String, Object>> itens) {

        Map<Double, Double[]> cachePrecos = new HashMap<>();
        Random random = new Random();

        for (Map<String, Object> item : itens) {
            Object valorObj = item.get("VALOR");
            double valorAtual = (valorObj instanceof Number) ? ((Number) valorObj).doubleValue() : 0.0;

            if (valorAtual == 0.0) {
                item.put("VALOR_PAPER", 0.0);
                item.put("VALOR_GRAFITE", 0.0);
                continue;
            }

            if (cachePrecos.containsKey(valorAtual)) {
                Double[] precosCalculados = cachePrecos.get(valorAtual);
                item.put("VALOR_PAPER", precosCalculados[0]);
                item.put("VALOR_GRAFITE", precosCalculados[1]);
                continue;
            }

            double minPctPaper, maxPctPaper, minPctGrafite, maxPctGrafite;

            if (valorAtual < 1.00) {
                minPctPaper = 0.20; maxPctPaper = 0.40;
                minPctGrafite = 0.15; maxPctGrafite = 0.40;
            } else if (valorAtual < 4.00) {
                minPctPaper = 0.20; maxPctPaper = 0.30;
                minPctGrafite = 0.15; maxPctGrafite = 0.25;
            } else if (valorAtual < 10.00) {
                minPctPaper = 0.10; maxPctPaper = 0.14;
                minPctGrafite = 0.14; maxPctGrafite = 0.18;
            } else if (valorAtual < 20.00) {
                minPctPaper = 0.08; maxPctPaper = 0.13;
                minPctGrafite = 0.11; maxPctGrafite = 0.15;
            } else if (valorAtual < 40.00) {
                minPctPaper = 0.08; maxPctPaper = 0.12;
                minPctGrafite = 0.05; maxPctGrafite = 0.10;
            } else if(valorAtual < 100.00){
                minPctPaper = 0.03; maxPctPaper = 0.10;
                minPctGrafite = 0.03; maxPctGrafite = 0.10;
            }else {
                minPctPaper = 0.02; maxPctPaper = 0.08;
                minPctGrafite = 0.02; maxPctGrafite = 0.08;
            }

            double pctPaper = minPctPaper + (maxPctPaper - minPctPaper) * random.nextDouble();
            double valorPaper = arredondarPara05(valorAtual * (1 + pctPaper));

            double pctGrafite = minPctGrafite + (maxPctGrafite - minPctGrafite) * random.nextDouble();
            double valorGrafite = arredondarPara05(valorAtual * (1 + pctGrafite));

            if (Math.abs(valorGrafite - valorPaper) <= 0.05) {
                valorGrafite = valorPaper + 0.10;
            }

            item.put("VALOR_PAPER", valorPaper);
            item.put("VALOR_GRAFITE", valorGrafite);

            cachePrecos.put(valorAtual, new Double[]{valorPaper, valorGrafite});
        }
    }

    public double calcularTotalGeral(List<Map<String, Object>> itens) {
        double total = 0.0;

        for (Map<String, Object> item : itens) {
            double quantidade = 0.0;
            Object qtObj = item.get("QT");

            if (qtObj instanceof Number) {
                quantidade = ((Number) qtObj).doubleValue();
            }

            Double valor = (Double) item.get("VALOR");

            if (valor != null) {
                total += (valor * quantidade);
            }
        }

        return total;
    }

    private double arredondarPara05(double valor) {
        return Math.round(valor * 20.0) / 20.0;
    }

    private boolean isCellEmpty(Cell cell) {
        return cell == null || cell.getCellType() == CellType.BLANK
                || (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty());
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}