package com.nordeste.livraria.api.automacao;

import org.springframework.stereotype.Service;
import br.com.caelum.stella.inwords.FormatoDeReal;
import br.com.caelum.stella.inwords.NumericToWordsConverter;
import com.deepoove.poi.XWPFTemplate;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class WordDocsService {

    public static void gerarConsolidacao(String caminhoModelo, String caminhoSaida,
                                         Map<String,String> dadosEscolas, List<Map<String, Object>> itens,
                                         double total){
        try {
            Map<String, Object> dadosFinais = new HashMap<>();

            for (Map.Entry<String, String> entry : dadosEscolas.entrySet()) {
                String chaveOriginal = entry.getKey();

                String chaveLimpa = chaveOriginal.replace("<", "").replace(">", "");

                dadosFinais.put(chaveLimpa, entry.getValue());
            }

            DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
            simbolos.setDecimalSeparator(',');

            DecimalFormat df = new DecimalFormat("0.00",simbolos);

            double totalPaper = 0.0;
            double totalGrafite = 0.0;

            int totalLinhasModelo = 50;

            for (int i = 0; i < totalLinhasModelo; i++) {
                int numeroItem = i + 1;

                String placeholder = "_" + numeroItem;

                if (i<itens.size()){
                    Map<String,Object> dadosLinha = itens.get(i);

                    double quantidade = 0.0;

                    Object qtObj = dadosLinha.get("QT");
                    if (qtObj instanceof Number){
                        quantidade = ((Number) qtObj).doubleValue();
                    }

                    dadosFinais.put("ITEM" + placeholder,dadosLinha.get("ITEM"));
                    dadosFinais.put("UN" + placeholder, dadosLinha.get("UN"));
                    dadosFinais.put("QT" + placeholder, dadosLinha.get("QT"));

                    Double valorPaper = (Double) dadosLinha.get("VALOR_PAPER");
                    Double valorGrafite = (Double) dadosLinha.get("VALOR_GRAFITE");

                    dadosFinais.put("vA" + placeholder, df.format(dadosLinha.get("VALOR")));
                    dadosFinais.put("vB" + placeholder, df.format(valorPaper));
                    dadosFinais.put("vC" + placeholder, df.format(valorGrafite));

                    if (valorPaper != null) totalPaper += (valorPaper * quantidade);
                    if (valorGrafite != null) totalGrafite += (valorGrafite * quantidade);
                }else {
                    dadosFinais.put("ITEM" + placeholder, "");
                    dadosFinais.put("UN" + placeholder, "");
                    dadosFinais.put("QT" + placeholder, "");
                    dadosFinais.put("vA" + placeholder, "");
                    dadosFinais.put("vB" + placeholder, "");
                    dadosFinais.put("vC" + placeholder, "");
                }
            }

            dadosFinais.put("TOTAL",df.format(total));
            dadosFinais.put("TOTAL_PAPER",df.format(totalPaper));
            dadosFinais.put("TOTAL_GRAFITE",df.format(totalGrafite));

            XWPFTemplate template = XWPFTemplate.compile(caminhoModelo);
            template.render(dadosFinais);
            template.writeToFile(caminhoSaida);
            template.close();

            System.out.println("Consolidação gerada!");

        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao gerar Consolidação: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void gerarRecibo(String caminhoModelo, String caminhoSaida,
                                   Map<String, String> dadosEscola, double total) {
        try {
            DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
            simbolos.setDecimalSeparator(',');
            DecimalFormat df = new DecimalFormat("0.00", simbolos);

            NumericToWordsConverter conversor = new NumericToWordsConverter(new FormatoDeReal());

            Map<String, Object> dados = new HashMap<>();

            for (Map.Entry<String, String> entry : dadosEscola.entrySet()) {
                dados.put(entry.getKey().replace("<", "").replace(">", ""), entry.getValue());
            }

            dados.put("TOTAL", df.format(total));
            dados.put("EXTENSO",conversor.toWords(total).toUpperCase());

            XWPFTemplate template = XWPFTemplate.compile(caminhoModelo);
            template.render(dados);
            template.writeToFile(caminhoSaida);
            template.close();

            System.out.println("Recibo gerado!");

        } catch (Exception e) {
            throw new RuntimeException("Erro no Recibo: " + e.getMessage());
        }
    }
}
