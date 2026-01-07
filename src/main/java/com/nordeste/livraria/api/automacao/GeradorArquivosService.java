package com.nordeste.livraria.api.automacao;

import com.nordeste.livraria.api.domain.dadosAutomacao.DadosEntradaAutomacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class GeradorArquivosService {

    @Autowired
    private DadosService dadosService;

    @Autowired
    private OrcamentosService orcamentosService;

    @Autowired
    private WordDocsService wordDocsService;


    public byte[] gerarArquivos(DadosEntradaAutomacao dadosEntrada) throws IOException {

        Map<String, String> dadosEscola = dadosService.lerDadosEscolas(dadosEntrada.escola());

        Map<String, String> dadosCabecalhos = dadosService.lerDadosAdicionais(dadosEscola, dadosEntrada);

        List<Map<String, Object>> itens = dadosService.lerArquivoDoador(dadosEntrada.arquivoDoador());

        dadosService.aplicarRegrasDeNegocio(itens);

        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
        simbolos.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("0.00", simbolos);
        double totalNce = dadosService.calcularTotalGeral(itens);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);


        String saidaNce = "ORÇAMENTO NF" + dadosCabecalhos.get("NF") + " " +
                dadosCabecalhos.get("ANO_ORCAMENTOS") + "-" + dadosCabecalhos.get("MES_ORCAMENTOS")
                + "-" + dadosCabecalhos.get("DIA_ORCAMENTOS") + " NCE.xlsx";
        InputStream modeloNce = getClass().getResourceAsStream("/templates/MODELO NCE JAVA.xlsx");

        byte[] bytesNce = orcamentosService.preencherNce(
                modeloNce,
                dadosCabecalhos,
                itens
        );
        adicionarAoZip(zipOutputStream,saidaNce,bytesNce);

        String saidaPaper = "ORÇAMENTO NF" + dadosCabecalhos.get("NF") + " " +
                dadosCabecalhos.get("ANO_ORCAMENTOS") + "-" + dadosCabecalhos.get("MES_ORCAMENTOS")
                + "-" + dadosCabecalhos.get("DIA_ORCAMENTOS") + " PAPER&CO.xlsx";
        InputStream modeloPaper = getClass().getResourceAsStream("/templates/MODELO PAPER JAVA.xlsx");

        byte[] bytesPaper = orcamentosService.preencherPaper(
                modeloPaper,
                dadosCabecalhos,
                itens
        );
        adicionarAoZip(zipOutputStream,saidaPaper,bytesPaper);

        String saidaGrafite = "ORÇAMENTO NF" + dadosCabecalhos.get("NF") + " " +
                dadosCabecalhos.get("ANO_ORCAMENTOS") + "-" + dadosCabecalhos.get("MES_ORCAMENTOS")
                + "-" + dadosCabecalhos.get("DIA_ORCAMENTOS") + " GRAFITE.xlsx";
        InputStream modeloGrafite = getClass().getResourceAsStream("/templates/MODELO GRAFITE JAVA.xlsx");

        byte[] bytesGrafte = orcamentosService.preencherGrafite(
                modeloGrafite,
                dadosCabecalhos,
                itens
        );
        adicionarAoZip(zipOutputStream,saidaGrafite,bytesGrafte);

        String saidaControle = "MODELO DOC NF" + dadosCabecalhos.get("NF") + " .xlsx";
        InputStream modeloControle = getClass().getResourceAsStream("/templates/MODELO CONTROLE.xlsx");

        byte[] bytesControle = orcamentosService.preencherControle(
                modeloControle,
                itens
        );
        adicionarAoZip(zipOutputStream,saidaControle,bytesControle);


        if (dadosEscola.get("TEM_CONSOLIDACAO").equals("S")) {

            String saidaCons = "ORÇAMENTO NF" + dadosCabecalhos.get("NF") + " " +
                    dadosCabecalhos.get("ANO_CONSOLIDACAO") + "-" + dadosCabecalhos.get("MES_CONSOLIDACAO")
                    + "-" + dadosCabecalhos.get("DIA_CONSOLIDACAO") + " CONSOLIDAÇÃO DE PESQ DE PREÇO.docx";
            InputStream modeloCons = getClass().getResourceAsStream("/templates/MODELO CONSOLIDACAO JAVA.docx");

            byte[] bytesCons = wordDocsService.gerarConsolidacao(modeloCons, dadosEscola, itens, totalNce);
            adicionarAoZip(zipOutputStream,saidaCons,bytesCons);
        }

        if (dadosEscola.get("TEM_RECIBO").equals("S")) {

            String saidaRecibo = "ORÇAMENTO NF" + dadosCabecalhos.get("NF") + " " +
                    dadosCabecalhos.get("ANO_R") + "-" + dadosCabecalhos.get("MES_R")
                    + "-" + dadosCabecalhos.get("DIA_R") + " RECIBO "+ df.format(totalNce) + " NCE.docx";
            InputStream modeloRecibo = getClass().getResourceAsStream("/templates/MODELO RECIBO.docx");

            byte[] bytesRecibo = wordDocsService.gerarRecibo(modeloRecibo, dadosEscola, totalNce);
            adicionarAoZip(zipOutputStream,saidaRecibo,bytesRecibo);

        }
        zipOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private void adicionarAoZip(ZipOutputStream outputStream, String nomeArquivo, byte[] conteudo) throws IOException {
        ZipEntry entry = new ZipEntry(nomeArquivo);
        outputStream.putNextEntry(entry);
        outputStream.write(conteudo);
        outputStream.closeEntry();
    }

}
