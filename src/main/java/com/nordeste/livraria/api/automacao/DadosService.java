/*
package com.nordeste.livraria.api.automacao;

import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class DadosService {

    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    public static List<Map<String,Object>> lerArquivoDoador(String caminho) throws FileNotFoundException {
        List<Map<String,Object>> dados = new ArrayList<>();

        try(FileInputStream inputStream = new FileInputStream(new File(caminho));
            Workbook workbook = new XSSFWorkbook(inputStream)) {
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

    public static Map<String,String> lerDadosEscolas(String caminho,String cnpj){

        Map<String,String> dadosEscola = new HashMap<>();

        try(FileInputStream inputStream = new FileInputStream(new File(caminho));
            Workbook workbook = new XSSFWorkbook(inputStream)){

            boolean encontrou = false;
            DataFormatter dataFormatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row:sheet){
                if (row.getRowNum() == 0) continue;

                String cnpjEmString = dataFormatter.formatCellValue(row.getCell(0)).trim();

                if (cnpjEmString.equals(cnpj)){

                    dadosEscola.put("<NOME>", dataFormatter.formatCellValue(row.getCell(1)));
                    dadosEscola.put("<CNPJ>", dataFormatter.formatCellValue(row.getCell(2)));
                    dadosEscola.put("<CEP>", dataFormatter.formatCellValue(row.getCell(3)));
                    dadosEscola.put("<CIDADE>", dataFormatter.formatCellValue(row.getCell(4)));
                    dadosEscola.put("<ENDEREÇO>", dataFormatter.formatCellValue(row.getCell(5)));
                    dadosEscola.put("DIRETOR", dataFormatter.formatCellValue(row.getCell(6)));

                    encontrou = true;
                    break;
                }
            }

            if (!encontrou){
                throw new RuntimeException("escola não encontrada");
            }

        }catch (IOException e){
            throw new RuntimeException(e);
        }

        System.out.println("Escola encontrada: " + dadosEscola.get("<NOME>"));

        return dadosEscola;
    }

    public static Map<String,String> lerDadosAdicionais(Map<String,String> dadosEscolas){

        String[] meses = {
                "JANEIRO", "FEVEREIRO", "MARÇO", "ABRIL", "MAIO", "JUNHO",
                "JULHO", "AGOSTO", "SETEMBRO", "OUTUBRO", "NOVEMBRO", "DEZEMBRO"
        };

        System.out.println("Digite o número da nota-");
        String nf = scanner.nextLine();
        dadosEscolas.put("NF",nf);

        System.out.println("Data dos orçamentos(dia)-");
        String diaOrcamentos = scanner.nextLine();
        dadosEscolas.put("DIA_ORCAMENTOS",diaOrcamentos);

        int mesOrcamentos = verificarMesValido();

        dadosEscolas.put("MES_ORCAMENTOS", String.valueOf(mesOrcamentos));

        System.out.println("Data dos orçamentos(ano)-");
        String anoOrcamentos = scanner.nextLine();
        dadosEscolas.put("ANO_ORCAMENTOS",anoOrcamentos);

        String dataOrcamentos = diaOrcamentos + " DE " + meses[mesOrcamentos-1] + " DE " + anoOrcamentos;
        dadosEscolas.put("<DATA>",dataOrcamentos);

        System.out.println("Consolidação? (S/N)");
        String temConsolidacaoStr = scanner.nextLine();


        if (temConsolidacaoStr.equalsIgnoreCase("S")){

            dadosEscolas.put("TEM_CONSOLIDACAO","S");

            System.out.println("Data da Consolidacao(dia)-");
            String diaConsolidacao = scanner.nextLine();
            dadosEscolas.put("DIA_CONSOLIDACAO",diaConsolidacao);

            int mesConsolidacao = verificarMesValido();
            dadosEscolas.put("MES_CONSOLIDACAO", String.valueOf(mesConsolidacao));

            System.out.println("Data da Consolidacao(ano)-");
            String anoConsolidacao = scanner.nextLine();
            dadosEscolas.put("ANO_CONSOLIDACAO",anoConsolidacao);

            String dataConsolidacao = diaConsolidacao + " de " +
                    OrcamentosService.formatarTextoTitle(meses[mesConsolidacao-1]) +
                    " de " + anoConsolidacao;
            dadosEscolas.put("DATA_CONS",dataConsolidacao);


        } else if (temConsolidacaoStr.equalsIgnoreCase("N")) {
            dadosEscolas.put("TEM_CONSOLIDACAO","N");
        } else {
            System.out.println("Resposta inválida,considerando como NÃO");
            dadosEscolas.put("TEM_CONSOLIDACAO","N");
        }

        System.out.println("Recibo? (S/N)");
        String temReciboStr = scanner.nextLine();

        if (temReciboStr.equalsIgnoreCase("S")){

            dadosEscolas.put("TEM_RECIBO","S");

            System.out.println("Data do Recibo(dia)-");
            String diaRecibo = scanner.nextLine();
            dadosEscolas.put("DIA_R",diaRecibo);

            int mesRecibo = verificarMesValido();
            dadosEscolas.put("MES_R", String.valueOf(mesRecibo));
            dadosEscolas.put("MES_R_EXTENSO",meses[mesRecibo -1]);

            System.out.println("Data do Recibo(ano)-");
            String anoRecibo = scanner.nextLine();
            dadosEscolas.put("ANO_R",anoRecibo);

            System.out.println("Mesma data da nota(S/N)");
            String dataNota = scanner.nextLine();

            if (dataNota.equalsIgnoreCase("N")){
                System.out.println("Data da Nota(dia)-");
                String diaNota = scanner.nextLine();
                dadosEscolas.put("DIA_N",diaNota);

                System.out.println("Data da Nota(mês)-");
                String mesNota = scanner.nextLine();
                dadosEscolas.put("MES_N",mesNota);

                System.out.println("Data da Nota(ano)-");
                String anoNota = scanner.nextLine();
                dadosEscolas.put("ANO_N",anoNota);
            } else if (dataNota.equalsIgnoreCase("S")) {
                dadosEscolas.put("DIA_N",diaRecibo);
                dadosEscolas.put("MES_N",String.valueOf(mesRecibo));
                dadosEscolas.put("ANO_N",anoRecibo);
            }else {
                System.out.println("Resposta inválida,considerando como NÃO");
                dadosEscolas.put("DIA_N",diaRecibo);
                dadosEscolas.put("MES_N",String.valueOf(mesRecibo));
                dadosEscolas.put("ANO_N",anoRecibo);
            }

            System.out.println("Pago por meio de-");
            String pagoPor = scanner.nextLine();
            dadosEscolas.put("MEIO",pagoPor.toUpperCase());

        } else if (temReciboStr.equalsIgnoreCase("N")) {
            dadosEscolas.put("TEM_RECIBO","N");
        }else {
            System.out.println("Resposta inválida,considerando como NÃO");
            dadosEscolas.put("TEM_RECIBO","N");
        }

        return dadosEscolas;
    }

    public static void aplicarRegrasDeNegocio(List<Map<String, Object>> itens) {

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

            if (valorAtual < 5.00) {
                minPctPaper = 0.25; maxPctPaper = 0.30;
                minPctGrafite = 0.30; maxPctGrafite = 0.35;
            } else if (valorAtual < 50.00) {
                minPctPaper = 0.10; maxPctPaper = 0.20;
                minPctGrafite = 0.10; maxPctGrafite = 0.20;
            } else {
                minPctPaper = 0.08; maxPctPaper = 0.12;
                minPctGrafite = 0.08; maxPctGrafite = 0.13;
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

    public static double calcularTotalGeral(List<Map<String, Object>> itens) {
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

    private static int verificarMesValido() {

        String[] meses = {
                "JANEIRO", "FEVEREIRO", "MARÇO", "ABRIL", "MAIO", "JUNHO",
                "JULHO", "AGOSTO", "SETEMBRO", "OUTUBRO", "NOVEMBRO", "DEZEMBRO"
        };

        int mesOrcamentos = 0;
        boolean mesValido = false;

        while (!mesValido) {
            try {
                System.out.println("(mês 1-12):");
                String entrada = scanner.nextLine();

                int mesDigitado = Integer.parseInt(entrada);

                String teste = meses[mesDigitado - 1];

                mesOrcamentos = mesDigitado;
                mesValido = true;


            } catch (NumberFormatException e) {
                System.out.println("Erro: Você digitou letras. Digite apenas NÚMEROS (ex: 5).");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Erro: Mês inexistente. Digite um número entre 1 e 12.");
            }
        }
        return mesOrcamentos;
    }


    private static double arredondarPara05(double valor) {
        return Math.round(valor * 20.0) / 20.0;
    }

    private static boolean isCellEmpty(Cell cell) {
        return cell == null || cell.getCellType() == CellType.BLANK
                || (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty());
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}

 */