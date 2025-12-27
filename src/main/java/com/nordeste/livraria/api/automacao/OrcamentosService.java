package com.nordeste.livraria.api.automacao;

import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class OrcamentosService {

    public static void preencherNce(String caminhoEntrada, String caminhoSaida,
                                    Map<String,String> dadosCabecalho, List<Map<String,Object>> itens) throws IOException {

        try(FileInputStream inputStream = new FileInputStream(new File(caminhoEntrada));
            Workbook workbook = new XSSFWorkbook(inputStream)){

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 5; i < 11; i++) {
                Row row = sheet.getRow(i);
                if (row != null){
                    for (Cell cell:row){
                        if (cell.getCellType() == CellType.STRING){
                            String texto = cell.getStringCellValue();

                            for (Map.Entry<String,String> entry : dadosCabecalho.entrySet()){
                                if (texto.contains(entry.getKey())){
                                    texto = texto.replace(entry.getKey(),entry.getValue());
                                    cell.setCellValue(texto);
                                }
                            }
                        }
                    }
                }
            }

            int linhaInicialTabela = 14;

            for (Map<String,Object> item:itens){
                Row row = sheet.getRow(linhaInicialTabela);

                if (row == null) {
                    System.out.println("AVISO: O template nce acabou na linha " + linhaInicialTabela + ". Item ignorado.");
                    break;
                }

                Cell cellItem = row.getCell(1);
                if (cellItem == null) cellItem = row.createCell(1);
                cellItem.setCellValue(String.valueOf(item.getOrDefault("ITEM","")));

                Cell cellUn = row.getCell(2);
                if (cellUn == null) cellUn = row.createCell(2);
                cellUn.setCellValue(String.valueOf(item.getOrDefault("UN","")));

                Cell cellQT = row.getCell(3);
                if (cellQT == null) cellQT = row.createCell(3);
                Object qtObj = item.get(("QT"));
                double qtValue = (qtObj instanceof Number)? ((Number) qtObj).doubleValue():0.0;
                cellQT.setCellValue(qtValue);

                Cell cellValor = row.getCell(4);
                if (cellValor == null) cellValor = row.createCell(4);
                Object valorObj = item.get("VALOR");
                double valorValue = (valorObj instanceof Number)? ((Number) valorObj).doubleValue():0.0;
                cellValor.setCellValue(valorValue);

                linhaInicialTabela++;
            }

            workbook.setForceFormulaRecalculation(true);

            try(FileOutputStream outputStream = new FileOutputStream(new File(caminhoSaida))){
                workbook.write(outputStream);
            }
            System.out.println("NCE gerado.");

        }catch (IOException e){
            throw new RuntimeException("Erro ao processar NCE: " + e.getMessage());
        }
    }

    public static void preencherPaper(String caminhoEntrada, String caminhoSaida,
                                      Map<String,String> dadosCabecalho, List<Map<String,Object>> itens){

        try(FileInputStream inputStream = new FileInputStream(new File(caminhoEntrada));
            Workbook workbook = new XSSFWorkbook(inputStream)){

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 6; i < 13; i++) {
                Row row = sheet.getRow(i);
                if (row != null){
                    for (Cell cell:row){
                        if (cell.getCellType() == CellType.STRING){
                            String texto = cell.getStringCellValue();

                            for (Map.Entry<String,String> entry : dadosCabecalho.entrySet()){
                                if (texto.contains(entry.getKey())){
                                    texto = texto.replace(entry.getKey(),entry.getValue());
                                    cell.setCellValue(texto);
                                }
                            }
                        }
                    }
                }
            }

            int linhaInicialTabela = 16;

            for (Map<String,Object> item:itens){
                Row row = sheet.getRow(linhaInicialTabela);

                if (row == null) {
                    System.out.println("AVISO: O template paper acabou na linha " + linhaInicialTabela + ". Item ignorado.");
                    break;
                }

                Cell cellItem = row.getCell(1);
                if (cellItem == null) cellItem = row.createCell(1);
                cellItem.setCellValue(String.valueOf(item.getOrDefault("ITEM","")));

                Cell cellUn = row.getCell(2);
                if (cellUn == null) cellUn = row.createCell(2);
                cellUn.setCellValue(String.valueOf(item.getOrDefault("UN","")));

                Cell cellQT = row.getCell(3);
                if (cellQT == null) cellQT = row.createCell(3);
                Object qtObj = item.get(("QT"));
                double qtValue = (qtObj instanceof Number)? ((Number) qtObj).doubleValue():0.0;
                cellQT.setCellValue(qtValue);

                Cell cellValor = row.getCell(4);
                if (cellValor == null) cellValor = row.createCell(4);
                Object valorObj = item.get("VALOR_PAPER");
                double valorValue = (valorObj instanceof Number)? ((Number) valorObj).doubleValue():0.0;
                cellValor.setCellValue(valorValue);

                linhaInicialTabela++;
            }

            workbook.setForceFormulaRecalculation(true);

            try(FileOutputStream outputStream = new FileOutputStream(new File(caminhoSaida))){
                workbook.write(outputStream);
            }
            System.out.println("PAPER&CO gerado.");

        }catch (IOException e){
            throw new RuntimeException("Erro ao processar PAPER: " + e.getMessage());
        }
    }

    public static void preencherGrafite(String caminhoEntrada, String caminhoSaida,
                                        Map<String,String> dadosCabecalho, List<Map<String,Object>> itens){

        try(FileInputStream inputStream = new FileInputStream(new File(caminhoEntrada));
            Workbook workbook = new XSSFWorkbook(inputStream)){

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 6; i < 10; i++) {
                Row row = sheet.getRow(i);
                if (row != null){
                    for (Cell cell:row){
                        if (cell.getCellType() == CellType.STRING){
                            String texto = cell.getStringCellValue();

                            for (Map.Entry<String,String> entry : dadosCabecalho.entrySet()){
                                if (texto.contains(entry.getKey())){
                                    texto = texto.replace(entry.getKey(),entry.getValue());
                                    String textoFormatado = formatarTextoTitle(texto);
                                    cell.setCellValue(textoFormatado);
                                }
                            }
                        }
                    }
                }
            }

            int linhaInicialTabela = 11;

            for (Map<String,Object> item:itens){
                Row row = sheet.getRow(linhaInicialTabela);

                if (row == null) {
                    System.out.println("AVISO: O template grafite acabou na linha " + linhaInicialTabela + ". Item ignorado.");
                    break;
                }

                Cell cellItem = row.getCell(1);
                if (cellItem == null) cellItem = row.createCell(1);
                cellItem.setCellValue(String.valueOf(item.getOrDefault("ITEM","")));

                Cell cellUn = row.getCell(2);
                if (cellUn == null) cellUn = row.createCell(2);
                cellUn.setCellValue(String.valueOf(item.getOrDefault("UN","")));

                Cell cellQT = row.getCell(3);
                if (cellQT == null) cellQT = row.createCell(3);
                Object qtObj = item.get(("QT"));
                double qtValue = (qtObj instanceof Number)? ((Number) qtObj).doubleValue():0.0;
                cellQT.setCellValue(qtValue);

                Cell cellValor = row.getCell(4);
                if (cellValor == null) cellValor = row.createCell(4);
                Object valorObj = item.get("VALOR_GRAFITE");
                double valorValue = (valorObj instanceof Number)? ((Number) valorObj).doubleValue():0.0;
                cellValor.setCellValue(valorValue);

                linhaInicialTabela++;
            }

            workbook.setForceFormulaRecalculation(true);

            try(FileOutputStream outputStream = new FileOutputStream(new File(caminhoSaida))){
                workbook.write(outputStream);
            }
            System.out.println("GRAFITE gerado.");

        }catch (IOException e){
            throw new RuntimeException("Erro ao processar GRAFITE: " + e.getMessage());
        }
    }

    public static void preencherControle(String caminhoEntrada, String caminhoSaida,
                                         List<Map<String,Object>> itens){

        try(FileInputStream inputStream = new FileInputStream(new File(caminhoEntrada));
            Workbook workbook = new XSSFWorkbook(inputStream)){

            Sheet sheet = workbook.getSheetAt(0);

            int linhaInicialTabela = 2;

            for (Map<String,Object> item:itens){
                Row row = sheet.getRow(linhaInicialTabela);

                if (row == null) {
                    System.out.println("AVISO: O template do controle acabou na linha " + linhaInicialTabela + ". Item ignorado.");
                    break;
                }

                Cell cellItem = row.getCell(1);
                if (cellItem == null) cellItem = row.createCell(1);
                cellItem.setCellValue(String.valueOf(item.getOrDefault("ITEM","")));

                Cell cellUn = row.getCell(2);
                if (cellUn == null) cellUn = row.createCell(2);
                cellUn.setCellValue(String.valueOf(item.getOrDefault("UN","")));

                Cell cellQT = row.getCell(3);
                if (cellQT == null) cellQT = row.createCell(3);
                Object qtObj = item.get(("QT"));
                double qtValue = (qtObj instanceof Number)? ((Number) qtObj).doubleValue():0.0;
                cellQT.setCellValue(qtValue);

                Cell cellValor = row.getCell(4);
                if (cellValor == null) cellValor = row.createCell(4);
                Object valorObj = item.get("VALOR");
                double valorValue = (valorObj instanceof Number)? ((Number) valorObj).doubleValue():0.0;
                cellValor.setCellValue(valorValue);

                Cell cellValorPaper = row.getCell(7);
                if (cellValorPaper == null) cellValorPaper = row.createCell(4);
                Object valorObjPaper = item.get("VALOR_PAPER");
                double valorValuePaper = (valorObjPaper instanceof Number)? ((Number) valorObjPaper).doubleValue():0.0;
                cellValorPaper.setCellValue(valorValuePaper);

                Cell cellValorGrafite = row.getCell(10);
                if (cellValorGrafite == null) cellValorGrafite = row.createCell(4);
                Object valorObjGrafite = item.get("VALOR_GRAFITE");
                double valorValueGrafite = (valorObjGrafite instanceof Number)? ((Number) valorObjGrafite).doubleValue():0.0;
                cellValorGrafite.setCellValue(valorValueGrafite);

                linhaInicialTabela++;
            }

            workbook.setForceFormulaRecalculation(true);

            try(FileOutputStream outputStream = new FileOutputStream(new File(caminhoSaida))){
                workbook.write(outputStream);
            }
            System.out.println("CONTROLE gerado.");

        }catch (IOException e){
            throw new RuntimeException("Erro ao processar CONTROLE: " + e.getMessage());
        }
    }

    public static String formatarTextoTitle(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }

        String[] palavras = texto.toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        List<String> ignorar = List.of("de", "da", "do");

        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                if (ignorar.contains(palavra)) {
                    resultado.append(palavra);
                } else {
                    resultado.append(Character.toUpperCase(palavra.charAt(0)))
                            .append(palavra.substring(1));
                }
                resultado.append(" ");
            }
        }

        return resultado.toString().trim();
    }
}
