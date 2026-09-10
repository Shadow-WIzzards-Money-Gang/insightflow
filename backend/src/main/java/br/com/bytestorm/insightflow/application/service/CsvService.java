package br.com.bytestorm.insightflow.application.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;

/**
 * Mecanica de geracao de CSV. Nao conhece o dominio: recebe o cabecalho + as
 * linhas ja como texto e devolve o conteudo do arquivo.
 *
 * Formato voltado para o Excel pt-BR: separador ';' e UTF-8 com BOM.
 */
@Service
public class CsvService {

    private static final Logger log = LoggerFactory.getLogger(CsvService.class);

    private static final char SEPARADOR = ';';
    private static final char BOM = 0xFEFF;

    public String gerar(List<String> cabecalho, List<List<String>> linhas) {
        CSVFormat formato = CSVFormat.DEFAULT.builder()
            .setDelimiter(SEPARADOR)
            .setHeader(cabecalho.toArray(String[]::new))
            .setRecordSeparator("\r\n")
            .build();

        StringWriter writer = new StringWriter();
        writer.append(BOM);

        try (CSVPrinter printer = new CSVPrinter(writer, formato)) {
            for (List<String> linha : linhas) {
                printer.printRecord(linha);
            }
        } catch (IOException e) {
            // StringWriter nao lanca IOException na pratica.
            throw new UncheckedIOException("Falha ao gerar o CSV", e);
        }

        log.info("CSV gerado - {} linha(s), {} coluna(s)", linhas.size(), cabecalho.size());
        return writer.toString();
    }

    public List<String> montarLinhaCsv(AnaliseReuniao analise) {
        Reuniao reuniao = analise.getReuniao();
        SegmentoCliente segmento = reuniao.getSegmentoCliente();

        return Arrays.asList(
            String.valueOf(analise.getId()),
            analise.getAssunto(),
            analise.getPontosPositivos(),
            analise.getPontosNegativos(),
            String.valueOf(analise.getNota()),
            analise.getSentimentoReuniao().name(),
            analise.getRiscoCancelamento().name(),
            analise.getMotivoCancelamento(),
            analise.getProdutoTotvs().getNome(),
            segmento == null ? null : segmento.getNome(),
            reuniao.getDataReuniao() == null ? null : reuniao.getDataReuniao().toString(),
            reuniao.getDuracao() == null ? null : reuniao.getDuracao().toString(),
            reuniao.getHashTranscricao(),
            limparTexto(reuniao.getTranscricaoBruta())
        );
    }

    private String limparTexto(String texto) {
        if (texto == null) return null;
        return texto.replaceAll("[\\r\\n]+", " ").trim();
    }
}
